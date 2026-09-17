# Student Placement Management System

A console-based **Student Placement Management System** written in **pure Java**.
It manages student profiles, companies, placement drives, applications, eligibility
checking, and selection/result tracking — all from a single command-line menu.

There is no frontend/UI framework, no database, and no external libraries. The
entire application is contained in **one file**: `PlacementSystem.java`.


---

## 1. Project Overview

College placement cells typically track student records, company drives, and
selection results manually or through scattered spreadsheets, making it hard to
know at a glance which students qualify for a given job drive, what stage each
application is at, or how placements are trending across branches.

This project is a simplified, single-file Java program that models the entire
placement process end-to-end: registering students and companies, creating
placement drives with configurable eligibility rules, applying, automatically
checking eligibility, moving applications through a selection pipeline, and
generating placement statistics — all through a console menu, with **no external
dependencies**.

It's built as a learning/demo project (e.g. for a college DBMS/OOP assignment),
prioritizing clear business logic over a full production stack.

---

## 2. Features

- **Student profile management** — add/view students with branch, graduation
  year, CGPA, backlog count, and skill set
- **Company management** — add/view companies with sector and HR contact info
- **Placement drive management** — create drives with a package, date, and a
  full set of eligibility criteria (min CGPA, allowed branches, eligible
  graduation years, max backlogs, required skills)
- **Automatic, multi-criteria eligibility checking** — every criterion is
  checked at once, and every failing reason is reported (not just the first)
- **Application management** — students apply to drives; duplicate applications,
  applying to closed drives, and "downgrading" an existing placement are all
  blocked with clear error messages
- **Selection / result tracking** — a proper state machine: `ELIGIBLE →
  SHORTLISTED → SELECTED / REJECTED`, with a student's placement record
  auto-updated on selection
- **Reports & statistics** — placement % by branch, average package by branch,
  selections by company, top package earners, unplaced/placed student lists
- **Robust input validation** — every input field (name, email, CGPA, backlogs,
  graduation year, branch, date, package, etc.) re-prompts on invalid input
  instead of crashing or discarding the whole form
- **Case-sensitive skill matching** — `"Java"` and `"java"` are treated as
  distinct skills, matching how real applicant-tracking systems compare tags

---

## 3. Module Explanation

The single file is internally organized into five logical sections (all as
nested `static` classes, so there's still a clean separation of concerns even
though it's one `.java` file):

### 3.1 Enums
- `Branch` — CSE, IT, ECE, EEE, MECH, CIVIL, AIML, OTHER
- `ApplicationStatus` — APPLIED, ELIGIBLE, INELIGIBLE, SHORTLISTED, SELECTED, REJECTED

### 3.2 Models (data classes)
| Class | Represents |
|---|---|
| `Student` | A student's profile and current placement status |
| `Company` | A recruiting company |
| `PlacementDrive` | A job opening from a company, carrying its own eligibility criteria |
| `Application` | A student's application to a drive, with status and remarks |

### 3.3 Repositories (in-memory "database")
`StudentRepository`, `CompanyRepository`, `DriveRepository`,
`ApplicationRepository` — each is a simple wrapper around a `LinkedHashMap`
acting as a table, with `save`, `findById`, and `findAll` methods. All data
lives only in memory for the duration of the program run.

### 3.4 Services (business logic)
| Service | Responsibility |
|---|---|
| `EligibilityService` | Checks a student against a drive's CGPA, branch, graduation year, backlog, and skill requirements. Returns every failing reason, not just the first. |
| `ApplicationService` | Handles `apply()` — enforces rules (no duplicate applications, no applying to closed drives, no "downgrade" applications) and runs the eligibility check automatically |
| `SelectionService` | Drives the selection state machine: shortlist → select/reject. Only valid transitions are allowed (e.g. you can't select an application that hasn't been shortlisted) |
| `ReportService` | Read-only analytical queries: placement % by branch, average package by branch, top earners, selections per company, etc. |

### 3.5 Main (console menu)
Wires all repositories and services together and exposes a numbered menu for
every operation, with input-validation helper methods (`readValidName`,
`readGraduationYear`, `readCgpaInRange`, `readValidEmail`, `readValidDate`,
`readBranchSet`, `readYearSet`, etc.) that loop until valid input is given.

## 4. Architecture / Flow Diagram

Student --applies--> PlacementDrive
                        |
                EligibilityService
        (checks CGPA, branch, grad year,
             backlogs, required skills)
                        |
              ELIGIBLE / INELIGIBLE
                        |
        SelectionService.shortlist()
                        |
                 SHORTLISTED
                    /       \
        select()            reject()
           |                    |
        SELECTED             REJECTED
           |
  Student.placed = true
  (company + package recorded)
           |
     ReportService
  (placement %, avg package,
   top earners, by branch, etc.)

**Layer architecture:**

┌─────────────────────────────────────────┐
│  Main (console menu, input validation)   │
├─────────────────────────────────────────┤
│  Services (business logic)               │
│  EligibilityService · ApplicationService │
│  SelectionService · ReportService        │
├─────────────────────────────────────────┤
│  Repositories (in-memory "database")     │
│  StudentRepository · CompanyRepository   │
│  DriveRepository · ApplicationRepository │
├─────────────────────────────────────────┤
│  Models (plain data classes)             │
│  Student · Company · PlacementDrive      │
│  Application                             │
└─────────────────────────────────────────┘

Each layer only talks to the layer directly below it — the console menu never
touches a repository directly, and services never print to the console. This
means the repository layer could later be swapped for real JDBC/database code
without changing any business logic.

## 5. How to Run

cd path/to/project-folder

javac PlacementSystem.java

java PlacementSystem

You'll see a numbered menu:

========== Student Placement Management System ==========
 1. Add Student            2. List Students
 3. Add Company            4. List Companies
 5. Create Placement Drive 6. List Drives
 7. Apply to Drive         8. Check Eligibility (preview)
 9. List Applications     10. Shortlist Application
11. Select Application    12. Reject Application
13. Reports & Statistics   0. Exit
Choose an option:

The app starts with **no data** — add a student, company, and drive yourself
(options 1, 3, 5) before testing eligibility/applications. Choose `0` to exit.


## 6. Testing

There is no external test framework (e.g. JUnit) — this is a console app meant
to be exercised through its menu, either manually or via scripted input. Below
is a suggested test plan covering the main flows and edge cases.

Run `java PlacementSystem` and step through in this order:

| # | Action | Menu option | Expected result |
|---|---|---|---|
| 1 | Add a student (e.g. CGPA 8.5, CSE, 2026, skills: Java, SQL) | 1 | Student saved with ID `STU-1` |
| 2 | Add a company | 3 | Company saved with ID `CMP-1` |
| 3 | Create a drive (min CGPA 7.0, branch CSE, skill Java) | 5 | Drive saved with ID `DRV-1` |
| 4 | Check eligibility of STU-1 vs DRV-1 | 8 | ELIGIBLE |
| 5 | Apply STU-1 to DRV-1 | 7 | Application saved, status ELIGIBLE |
| 6 | Apply STU-1 to DRV-1 again | 7 | Error: "already applied" |
| 7 | Shortlist the application | 10 | Status → SHORTLISTED |
| 8 | Shortlist it again | 10 | Error: expected status ELIGIBLE but was SHORTLISTED |
| 9 | Select the application | 11 | Status → SELECTED; student's placement record updates |
| 10 | List students | 2 | Student now shows `Placed: YES` with company + package |
| 11 | Apply the same student to a lower-package drive | 7 | Error: "already placed... cannot apply to a drive offering X LPA or less" |
| 12 | View reports | 13 | Placement %, branch stats, top earners all reflect the above |

## 7. File Structure
project-folder/
└── PlacementSystem.java   # entire application: enums, models, in-memory
                            # repositories, business logic services, and
                            # the console menu

