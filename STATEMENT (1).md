# Problem Statement

## Problem Statement

College placement cells manage student records, company drives, and selection
results manually or through scattered spreadsheets. This makes it hard to check
which students are eligible for a given job drive, track application status, and
generate placement statistics. This project builds a simple system to manage the
end-to-end placement process — from student registration to final selection —
with automated eligibility checking based on CGPA, branch, graduation year,
backlogs, and skills.

## Scope of the Project

- Manage student profiles, companies, and placement drives
- Allow students to apply to drives and automatically check eligibility
- Track applications through shortlisting, selection, and rejection
- Generate placement reports and statistics
- Console-based Java application with in-memory data storage (no database,
  no persistence across runs, no GUI/web frontend)

## Target Users

- **Placement Cell / Training & Placement Officer (TPO)** — adds companies and
  drives, reviews applications, shortlists and selects candidates, views reports
- **Students** — profiles are registered in the system and matched against
  drive eligibility criteria (in this version, added on their behalf via the
  same console menu)

## High-Level Features

- Student profile management (CGPA, branch, graduation year, backlogs, skills)
- Company management
- Placement drive creation with configurable eligibility criteria
- Automated eligibility checking (multi-criteria)
- Application management (apply, prevent duplicates, enforce business rules)
- Selection/result tracking (Shortlisted → Selected / Rejected)
- Placement reports and statistics (by branch, by company, top packages, etc.)
