from flask import Flask, render_template, request, redirect, session
import sqlite3

app = Flask(__name__)
app.secret_key = "resq_secret_key"


# =====================================================
# DATABASE CONNECTION (USED BY ALL SECTIONS)
# =====================================================
def get_db_connection():
    conn = sqlite3.connect("database.db")
    conn.row_factory = sqlite3.Row
    return conn


# =====================================================
# USER REPORTING SECTION
# Route: /
# Purpose: Citizen submits animal report
# =====================================================
@app.route("/", methods=["GET", "POST"])
def home():
    if request.method == "POST":
        # collect condition checkboxes
        conditions_list = request.form.getlist("conditions")
        conditions = ", ".join(conditions_list)

        # optional description (only when "Other" selected)
        description = request.form.get("description")

        # location is mandatory
        location = request.form["location"]

        # insert report into database
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute(
            "INSERT INTO reports (conditions, description, location) VALUES (?, ?, ?)",
            (conditions, description, location)
        )
        conn.commit()

        # get auto-generated report ID
        report_id = cursor.lastrowid
        conn.close()

        # show confirmation page
        return render_template("confirmation.html", report_id=report_id)

    return render_template("report.html")


# =====================================================
# USER TRACKING SECTION
# Route: /track
# Purpose: Citizen checks report status using Report ID
# =====================================================
@app.route("/track", methods=["GET", "POST"])
def track():
    report = None
    searched = False

    if request.method == "POST":
        searched = True
        report_id = request.form.get("report_id")

        conn = get_db_connection()
        report = conn.execute(
            "SELECT * FROM reports WHERE id = ?",
            (report_id,)
        ).fetchone()
        conn.close()

    return render_template(
        "track.html",
        report=report,
        searched=searched
    )
# =====================================================
# ADOPTION – VIEW ADOPTABLE ANIMALS (PUBLIC)
# Route: /adopt
# =====================================================
@app.route("/adopt")
def adopt():
    conn = sqlite3.connect("database.db")
    cursor = conn.cursor()

    cursor.execute("SELECT id FROM reports WHERE status = 'Treated'")
    animals = cursor.fetchall()

    conn.close()
    return render_template("adopt.html", animals=animals)

# =========================================
# ADOPTION – ANIMAL DETAILS (PUBLIC)
# Route: /adopt/<report_id>
# =========================================
@app.route("/adopt/<int:report_id>")
def adopt_details(report_id):
    conn = sqlite3.connect("database.db")
    cursor = conn.cursor()

    cursor.execute("""
        SELECT id, conditions, description, location
        FROM reports
        WHERE id = ? AND status = 'Treated'
    """, (report_id,))
    report = cursor.fetchone()

    conn.close()

    if not report:
        return "Animal not available for adoption"

    return render_template("adopt_details.html", report=report)

# =========================================
# ADOPTION – SUBMIT INTEREST (PUBLIC)
# Route: /adopt_interest/<report_id>
# =========================================
@app.route("/adopt_interest/<int:report_id>", methods=["POST"])
def adopt_interest(report_id):
    name = request.form["name"]
    phone = request.form["phone"]
    message = request.form.get("message")

    conn = sqlite3.connect("database.db")
    cursor = conn.cursor()

    cursor.execute("""
        INSERT INTO adoption_requests (report_id, name, phone, message)
        VALUES (?, ?, ?, ?)
    """, (report_id, name, phone, message))

    conn.commit()
    conn.close()

    return "Adoption interest submitted successfully"

# =====================================================
# ADMIN LOGIN SECTION
# Route: /login
# Purpose: Admin authentication
# =====================================================
@app.route("/login", methods=["GET", "POST"])
def login():
    if request.method == "POST":
        if (
            request.form["username"] == "admin"
            and request.form["password"] == "resq123"
        ):
            session["admin"] = True
            return redirect("/admin")

        return render_template("login.html", error="Invalid credentials")

    return render_template("login.html")


# =====================================================
# ADMIN DASHBOARD SECTION
# Route: /admin
# Purpose: View all reports
# =====================================================
@app.route("/admin")
def admin():
    if not session.get("admin"):
        return redirect("/login")

    conn = get_db_connection()
    reports = conn.execute("SELECT * FROM reports").fetchall()
    conn.close()

    return render_template("admin.html", reports=reports)


# =====================================================
# ADMIN ACTION SECTION
# Route: /update_status
# Purpose: Admin updates report status
# =====================================================
@app.route("/update_status", methods=["POST"])
def update_status():
    if not session.get("admin"):
        return redirect("/login")

    conn = get_db_connection()
    conn.execute(
        "UPDATE reports SET status = ? WHERE id = ?",
        (request.form["status"], request.form["id"])
    )
    conn.commit()
    conn.close()

    return redirect("/admin")

# =========================================
# ADMIN – VIEW ADOPTION REQUESTS
# Route: /admin/adoptions
# =========================================
@app.route("/admin/adoptions")
def admin_adoptions():
    if not session.get("admin"):
        return redirect("/login")

    conn = sqlite3.connect("database.db")
    cursor = conn.cursor()

    cursor.execute("""
        SELECT id, report_id, name, phone, message, status
        FROM adoption_requests
        ORDER BY created_at DESC
    """)
    requests = cursor.fetchall()

    conn.close()

    return render_template("admin_adoptions.html", requests=requests)



# =====================================================
# LOGOUT SECTION
# Route: /logout
# Purpose: End admin session
# =====================================================
@app.route("/logout")
def logout():
    session.clear()
    return redirect("/login")


# =====================================================
# APP START
# =====================================================
if __name__ == "__main__":
    app.run(debug=True)
