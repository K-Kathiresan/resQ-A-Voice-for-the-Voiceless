package com.resq.resq.repository;

import com.resq.resq.model.Report;
import com.resq.resq.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByUser(User user);
}