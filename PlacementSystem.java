import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class PlacementSystem {


    enum Branch { CSE, IT, ECE, EEE, MECH, CIVIL, AIML, OTHER }

    enum ApplicationStatus {
        APPLIED,       
        ELIGIBLE,       
        INELIGIBLE,     
        SHORTLISTED,    
        SELECTED,       
        REJECTED        }


    static class Student {
        private final String studentId;
        private String name;
        private Branch branch;
        private int graduationYear;
        private double cgpa;
        private int backlogs;
        private Set<String> skills;
        private boolean placed;
        private String placedCompanyName;
        private double placedPackageLPA;

        Student(String studentId, String name, Branch branch, int graduationYear,
                double cgpa, int backlogs, Set<String> skills) {
            this.studentId = studentId;
            this.name = name;
            this.branch = branch;
            this.graduationYear = graduationYear;
            this.cgpa = cgpa;
            this.backlogs = backlogs;
            this.skills = new HashSet<>(skills);
            this.placed = false;
        }

        String getStudentId() { return studentId; }
        String getName() { return name; }
        Branch getBranch() { return branch; }
        int getGraduationYear() { return graduationYear; }
        double getCgpa() { return cgpa; }
        int getBacklogs() { return backlogs; }
        Set<String> getSkills() { return skills; }
        boolean isPlaced() { return placed; }
        String getPlacedCompanyName() { return placedCompanyName; }
        double getPlacedPackageLPA() { return placedPackageLPA; }

        void setCgpa(double cgpa) { this.cgpa = cgpa; }
        void setBacklogs(int backlogs) { this.backlogs = backlogs; }
        void addSkill(String skill) { this.skills.add(skill); }

        void markPlaced(String companyName, double packageLPA) {
            this.placed = true;
            this.placedCompanyName = companyName;
            this.placedPackageLPA = packageLPA;
        }

        @Override
        public String toString() {
            return String.format("Student[%s] %s | %s | GradYear:%d | CGPA:%.2f | Backlogs:%d | Skills:%s | Placed:%s%s",
                    studentId, name, branch, graduationYear, cgpa, backlogs, skills,
                    placed ? "YES" : "NO",
                    placed ? String.format(" (%s @ %.2f LPA)", placedCompanyName, placedPackageLPA) : "");
        }
    }

    static class Company {
        private final String companyId;
        private String name;
        private String sector;
        private String hrContactEmail;

        Company(String companyId, String name, String sector, String hrContactEmail) {
            this.companyId = companyId;
            this.name = name;
            this.sector = sector;
            this.hrContactEmail = hrContactEmail;
        }

        String getCompanyId() { return companyId; }
        String getName() { return name; }
        String getSector() { return sector; }
        String getHrContactEmail() { return hrContactEmail; }

        @Override
        public String toString() {
            return String.format("Company[%s] %s | Sector:%s | Contact:%s", companyId, name, sector, hrContactEmail);
        }
    }

    static class PlacementDrive {
        private final String driveId;
        private final Company company;
        private String jobTitle;
        private double packageLPA;
        private LocalDate driveDate;
        private double minCgpa;
        private int maxBacklogsAllowed;
        private Set<Branch> allowedBranches;
        private Set<Integer> eligibleGraduationYears;
        private Set<String> requiredSkills;
        private boolean closed;

        PlacementDrive(String driveId, Company company, String jobTitle, double packageLPA, LocalDate driveDate,
                       double minCgpa, int maxBacklogsAllowed, Set<Branch> allowedBranches,
                       Set<Integer> eligibleGraduationYears, Set<String> requiredSkills) {
            this.driveId = driveId;
            this.company = company;
            this.jobTitle = jobTitle;
            this.packageLPA = packageLPA;
            this.driveDate = driveDate;
            this.minCgpa = minCgpa;
            this.maxBacklogsAllowed = maxBacklogsAllowed;
            this.allowedBranches = new HashSet<>(allowedBranches);
            this.eligibleGraduationYears = new HashSet<>(eligibleGraduationYears);
            this.requiredSkills = new HashSet<>(requiredSkills);
            this.closed = false;
        }

        String getDriveId() { return driveId; }
        Company getCompany() { return company; }
        String getJobTitle() { return jobTitle; }
        double getPackageLPA() { return packageLPA; }
        LocalDate getDriveDate() { return driveDate; }
        double getMinCgpa() { return minCgpa; }
        int getMaxBacklogsAllowed() { return maxBacklogsAllowed; }
        Set<Branch> getAllowedBranches() { return allowedBranches; }
        Set<Integer> getEligibleGraduationYears() { return eligibleGraduationYears; }
        Set<String> getRequiredSkills() { return requiredSkills; }
        boolean isClosed() { return closed; }
        void close() { this.closed = true; }

        @Override
        public String toString() {
            return String.format(
                    "Drive[%s] %s @ %s | Package:%.2f LPA | Date:%s | MinCGPA:%.2f | MaxBacklogs:%d | Branches:%s | GradYears:%s | Skills:%s | %s",
                    driveId, jobTitle, company.getName(), packageLPA, driveDate, minCgpa, maxBacklogsAllowed,
                    allowedBranches, eligibleGraduationYears, requiredSkills, closed ? "CLOSED" : "OPEN");
        }
    }

    /** Application management module. */
    static class Application {
        private final String applicationId;
        private final Student student;
        private final PlacementDrive drive;
        private ApplicationStatus status;
        private final LocalDateTime appliedOn;
        private final List<String> remarks;

        Application(String applicationId, Student student, PlacementDrive drive) {
            this.applicationId = applicationId;
            this.student = student;
            this.drive = drive;
            this.status = ApplicationStatus.APPLIED;
            this.appliedOn = LocalDateTime.now();
            this.remarks = new ArrayList<>();
        }

        String getApplicationId() { return applicationId; }
        Student getStudent() { return student; }
        PlacementDrive getDrive() { return drive; }
        ApplicationStatus getStatus() { return status; }
        List<String> getRemarks() { return remarks; }

        void setStatus(ApplicationStatus status) { this.status = status; }
        void addRemark(String remark) { this.remarks.add(remark); }

        @Override
        public String toString() {
            return String.format("Application[%s] %s -> %s (%s) | Status:%s | Remarks:%s",
                    applicationId, student.getName(), drive.getJobTitle(), drive.getCompany().getName(),
                    status, remarks);
        }
    }


    static class IdGenerator {
        private final String prefix;
        private final AtomicInteger counter = new AtomicInteger(0);
        IdGenerator(String prefix) { this.prefix = prefix; }
        String next() { return prefix + "-" + counter.incrementAndGet(); }
    }

    static class StudentRepository {
        private final Map<String, Student> store = new LinkedHashMap<>();
        Student save(Student s) { store.put(s.getStudentId(), s); return s; }
        Optional<Student> findById(String id) { return Optional.ofNullable(store.get(id)); }
        List<Student> findAll() { return new ArrayList<>(store.values()); }
    }

    static class CompanyRepository {
        private final Map<String, Company> store = new LinkedHashMap<>();
        Company save(Company c) { store.put(c.getCompanyId(), c); return c; }
        Optional<Company> findById(String id) { return Optional.ofNullable(store.get(id)); }
        List<Company> findAll() { return new ArrayList<>(store.values()); }
    }

    static class DriveRepository {
        private final Map<String, PlacementDrive> store = new LinkedHashMap<>();
        PlacementDrive save(PlacementDrive d) { store.put(d.getDriveId(), d); return d; }
        Optional<PlacementDrive> findById(String id) { return Optional.ofNullable(store.get(id)); }
        List<PlacementDrive> findAll() { return new ArrayList<>(store.values()); }
    }

    static class ApplicationRepository {
        private final Map<String, Application> store = new LinkedHashMap<>();
        Application save(Application a) { store.put(a.getApplicationId(), a); return a; }
        Optional<Application> findById(String id) { return Optional.ofNullable(store.get(id)); }
        List<Application> findAll() { return new ArrayList<>(store.values()); }
        List<Application> findByStudentId(String studentId) {
            return store.values().stream().filter(a -> a.getStudent().getStudentId().equals(studentId))
                    .collect(Collectors.toList());
        }
    }

  
    static class EligibilityService {
        static class Result {
            final boolean eligible;
            final List<String> reasons;
            Result(boolean eligible, List<String> reasons) { this.eligible = eligible; this.reasons = reasons; }
        }

        Result checkEligibility(Student student, PlacementDrive drive) {
            List<String> reasons = new ArrayList<>();

            if (student.getCgpa() < drive.getMinCgpa()) {
                reasons.add(String.format("CGPA %.2f is below required minimum %.2f",
                        student.getCgpa(), drive.getMinCgpa()));
            }
            if (!drive.getAllowedBranches().isEmpty() && !drive.getAllowedBranches().contains(student.getBranch())) {
                reasons.add(String.format("Branch %s is not in allowed branches %s",
                        student.getBranch(), drive.getAllowedBranches()));
            }
            if (!drive.getEligibleGraduationYears().isEmpty()
                    && !drive.getEligibleGraduationYears().contains(student.getGraduationYear())) {
                reasons.add(String.format("Graduation year %d not in eligible years %s",
                        student.getGraduationYear(), drive.getEligibleGraduationYears()));
            }
            if (student.getBacklogs() > drive.getMaxBacklogsAllowed()) {
                reasons.add(String.format("Backlogs %d exceed maximum allowed %d",
                        student.getBacklogs(), drive.getMaxBacklogsAllowed()));
            }
            if (!drive.getRequiredSkills().isEmpty() && !student.getSkills().containsAll(drive.getRequiredSkills())) {
                List<String> missing = new ArrayList<>(drive.getRequiredSkills());
                missing.removeAll(student.getSkills());
                reasons.add("Missing required skills: " + missing);
            }
            if (drive.isClosed()) {
                reasons.add("Drive is closed for further applications");
            }
            return new Result(reasons.isEmpty(), reasons);
        }
    }

    static class ApplicationService {
        private final ApplicationRepository applicationRepository;
        private final EligibilityService eligibilityService;
        private final IdGenerator idGenerator = new IdGenerator("APP");

        ApplicationService(ApplicationRepository applicationRepository, EligibilityService eligibilityService) {
            this.applicationRepository = applicationRepository;
            this.eligibilityService = eligibilityService;
        }

        Application apply(Student student, PlacementDrive drive) {
            boolean alreadyApplied = applicationRepository.findByStudentId(student.getStudentId()).stream()
                    .anyMatch(a -> a.getDrive().getDriveId().equals(drive.getDriveId()));
            if (alreadyApplied) {
                throw new IllegalStateException(student.getName() + " has already applied to drive " + drive.getDriveId());
            }
            if (drive.isClosed()) {
                throw new IllegalStateException("Drive " + drive.getDriveId() + " is closed for applications");
            }
            if (student.isPlaced() && drive.getPackageLPA() <= student.getPlacedPackageLPA()) {
                throw new IllegalStateException(String.format(
                        "%s is already placed at %.2f LPA and cannot apply to a drive offering %.2f LPA or less",
                        student.getName(), student.getPlacedPackageLPA(), drive.getPackageLPA()));
            }

            Application application = new Application(idGenerator.next(), student, drive);
            EligibilityService.Result result = eligibilityService.checkEligibility(student, drive);
            if (result.eligible) {
                application.setStatus(ApplicationStatus.ELIGIBLE);
                application.addRemark("Eligibility check passed");
            } else {
                application.setStatus(ApplicationStatus.INELIGIBLE);
                result.reasons.forEach(application::addRemark);
            }
            return applicationRepository.save(application);
        }
    }

    static class SelectionService {
        void shortlist(Application application) {
            requireStatus(application, ApplicationStatus.ELIGIBLE, "shortlist");
            application.setStatus(ApplicationStatus.SHORTLISTED);
            application.addRemark("Shortlisted for interview round");
        }

        void select(Application application) {
            requireStatus(application, ApplicationStatus.SHORTLISTED, "select");
            application.setStatus(ApplicationStatus.SELECTED);
            application.addRemark("Selected - offer extended");

            Student student = application.getStudent();
            double newPackage = application.getDrive().getPackageLPA();
            String companyName = application.getDrive().getCompany().getName();
            if (!student.isPlaced() || newPackage > student.getPlacedPackageLPA()) {
                student.markPlaced(companyName, newPackage);
            }
        }

        void reject(Application application) {
            if (application.getStatus() != ApplicationStatus.SHORTLISTED
                    && application.getStatus() != ApplicationStatus.ELIGIBLE) {
                throw new IllegalStateException(
                        "Only ELIGIBLE or SHORTLISTED applications can be rejected. Current status: "
                                + application.getStatus());
            }
            application.setStatus(ApplicationStatus.REJECTED);
            application.addRemark("Rejected");
        }

        private void requireStatus(Application application, ApplicationStatus required, String action) {
            if (application.getStatus() != required) {
                throw new IllegalStateException(String.format(
                        "Cannot %s application %s: expected status %s but was %s",
                        action, application.getApplicationId(), required, application.getStatus()));
            }
        }
    }

    static class ReportService {
        private final StudentRepository studentRepository;
        private final ApplicationRepository applicationRepository;
        private final EligibilityService eligibilityService;

        ReportService(StudentRepository studentRepository, ApplicationRepository applicationRepository,
                      EligibilityService eligibilityService) {
            this.studentRepository = studentRepository;
            this.applicationRepository = applicationRepository;
            this.eligibilityService = eligibilityService;
        }

        List<Student> eligibleStudentsForDrive(PlacementDrive drive) {
            return studentRepository.findAll().stream()
                    .filter(s -> eligibilityService.checkEligibility(s, drive).eligible)
                    .collect(Collectors.toList());
        }

        List<Student> unplacedStudents() {
            return studentRepository.findAll().stream().filter(s -> !s.isPlaced()).collect(Collectors.toList());
        }

        List<Student> placedStudents() {
            return studentRepository.findAll().stream().filter(Student::isPlaced).collect(Collectors.toList());
        }

        Map<Branch, Long> placementCountByBranch() {
            return studentRepository.findAll().stream().filter(Student::isPlaced)
                    .collect(Collectors.groupingBy(Student::getBranch, Collectors.counting()));
        }

        Map<Branch, Double> averagePackageByBranch() {
            return studentRepository.findAll().stream().filter(Student::isPlaced)
                    .collect(Collectors.groupingBy(Student::getBranch, Collectors.averagingDouble(Student::getPlacedPackageLPA)));
        }

        Map<String, Long> selectionCountByCompany() {
            return applicationRepository.findAll().stream()
                    .filter(a -> a.getStatus() == ApplicationStatus.SELECTED)
                    .collect(Collectors.groupingBy(a -> a.getDrive().getCompany().getName(), Collectors.counting()));
        }

        List<Student> topPackageStudents(int n) {
            return studentRepository.findAll().stream().filter(Student::isPlaced)
                    .sorted(Comparator.comparingDouble(Student::getPlacedPackageLPA).reversed())
                    .limit(n).collect(Collectors.toList());
        }

        double overallPlacementPercentage() {
            List<Student> all = studentRepository.findAll();
            if (all.isEmpty()) return 0.0;
            long placedCount = all.stream().filter(Student::isPlaced).count();
            return (placedCount * 100.0) / all.size();
        }
    }


    private static final Scanner sc = new Scanner(System.in);

    private static final StudentRepository studentRepository = new StudentRepository();
    private static final CompanyRepository companyRepository = new CompanyRepository();
    private static final DriveRepository driveRepository = new DriveRepository();
    private static final ApplicationRepository applicationRepository = new ApplicationRepository();

    private static final EligibilityService eligibilityService = new EligibilityService();
    private static final ApplicationService applicationService = new ApplicationService(applicationRepository, eligibilityService);
    private static final SelectionService selectionService = new SelectionService();
    private static final ReportService reportService = new ReportService(studentRepository, applicationRepository, eligibilityService);

    private static final IdGenerator studentIdGen = new IdGenerator("STU");
    private static final IdGenerator companyIdGen = new IdGenerator("CMP");
    private static final IdGenerator driveIdGen = new IdGenerator("DRV");

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1": addStudent(); break;
                    case "2": listStudents(); break;
                    case "3": addCompany(); break;
                    case "4": listCompanies(); break;
                    case "5": createDrive(); break;
                    case "6": listDrives(); break;
                    case "7": applyToDrive(); break;
                    case "8": checkEligibility(); break;
                    case "9": listApplications(); break;
                    case "10": shortlistApplication(); break;
                    case "11": selectApplication(); break;
                    case "12": rejectApplication(); break;
                    case "13": showReports(); break;
                    case "0": running = false; break;
                    default: System.out.println("Invalid option, try again.");
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
            System.out.println();
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println("========== Student Placement Management System ==========");
        System.out.println(" 1. Add Student            2. List Students");
        System.out.println(" 3. Add Company            4. List Companies");
        System.out.println(" 5. Create Placement Drive 6. List Drives");
        System.out.println(" 7. Apply to Drive         8. Check Eligibility (preview)");
        System.out.println(" 9. List Applications     10. Shortlist Application");
        System.out.println("11. Select Application    12. Reject Application");
        System.out.println("13. Reports & Statistics   0. Exit");
        System.out.print("Choose an option: ");
    }


    private static void addStudent() {
        String name = readValidName("Name: ");
        Branch branch = readBranch();
        int gradYear = readGraduationYear();
        double cgpa = readValidCgpa();
        int backlogs = readNonNegativeInt("Backlogs: ");
        System.out.print("Skills (comma separated): ");
        Set<String> skills = parseCsv(sc.nextLine());

        Student student = new Student(studentIdGen.next(), name, branch, gradYear, cgpa, backlogs, skills);
        studentRepository.save(student);
        System.out.println("Added: " + student);
    }

    private static void listStudents() {
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) { System.out.println("No students yet."); return; }
        students.forEach(System.out::println);
    }


    private static void addCompany() {
        String name = readNonEmptyText("Company Name: ");
        String sector = readNonEmptyText("Sector: ");
        String email = readValidEmail("HR Contact Email: ");
        Company company = new Company(companyIdGen.next(), name, sector, email);
        companyRepository.save(company);
        System.out.println("Added: " + company);
    }

    private static void listCompanies() {
        List<Company> companies = companyRepository.findAll();
        if (companies.isEmpty()) { System.out.println("No companies yet."); return; }
        companies.forEach(System.out::println);
    }


    private static void createDrive() {
        Company company = pickCompany();
        if (company == null) return;

        String title = readNonEmptyText("Job Title: ");
        double pkg = readPositiveDouble("Package offered (LPA): ");
        LocalDate date = readValidDate("Drive Date (yyyy-mm-dd): ");
        double minCgpa = readCgpaInRange("Minimum CGPA required (0-10): ");
        int maxBacklogs = readNonNegativeInt("Maximum backlogs allowed: ");
        Set<Branch> branches = readBranchSet("Allowed branches (comma separated, blank = all): ");
        Set<Integer> years = readYearSet("Eligible graduation years (comma separated, blank = all): ");
        System.out.print("Required skills (comma separated, blank = none): ");
        Set<String> skills = parseCsv(sc.nextLine());

        PlacementDrive drive = new PlacementDrive(driveIdGen.next(), company, title, pkg, date,
                minCgpa, maxBacklogs, branches, years, skills);
        driveRepository.save(drive);
        System.out.println("Created: " + drive);
    }

    private static void listDrives() {
        List<PlacementDrive> drives = driveRepository.findAll();
        if (drives.isEmpty()) { System.out.println("No drives yet."); return; }
        drives.forEach(System.out::println);
    }


    private static void applyToDrive() {
        Student student = pickStudent();
        if (student == null) return;
        PlacementDrive drive = pickDrive();
        if (drive == null) return;
        Application application = applicationService.apply(student, drive);
        System.out.println("Application result: " + application);
    }

    private static void checkEligibility() {
        Student student = pickStudent();
        if (student == null) return;
        PlacementDrive drive = pickDrive();
        if (drive == null) return;
        EligibilityService.Result result = eligibilityService.checkEligibility(student, drive);
        System.out.println(student.getName() + " vs " + drive.getJobTitle() + ": "
                + (result.eligible ? "ELIGIBLE" : "NOT ELIGIBLE"));
        result.reasons.forEach(r -> System.out.println("  - " + r));
    }

    private static void listApplications() {
        List<Application> apps = applicationRepository.findAll();
        if (apps.isEmpty()) { System.out.println("No applications yet."); return; }
        apps.forEach(System.out::println);
    }

    private static void shortlistApplication() {
        Application app = pickApplication();
        if (app == null) return;
        selectionService.shortlist(app);
        System.out.println("Shortlisted: " + app);
    }

    private static void selectApplication() {
        Application app = pickApplication();
        if (app == null) return;
        selectionService.select(app);
        System.out.println("Selected: " + app);
        System.out.println("Updated student record: " + app.getStudent());
    }

    private static void rejectApplication() {
        Application app = pickApplication();
        if (app == null) return;
        selectionService.reject(app);
        System.out.println("Rejected: " + app);
    }


    private static void showReports() {
        System.out.println("-- Unplaced Students --");
        reportService.unplacedStudents().forEach(System.out::println);

        System.out.println("-- Placed Students --");
        reportService.placedStudents().forEach(System.out::println);

        System.out.println("-- Placement Count by Branch --");
        reportService.placementCountByBranch().forEach((b, c) -> System.out.println(b + ": " + c));

        System.out.println("-- Average Package by Branch (LPA) --");
        reportService.averagePackageByBranch().forEach((b, avg) -> System.out.printf("%s: %.2f%n", b, avg));

        System.out.println("-- Selections by Company --");
        reportService.selectionCountByCompany().forEach((c, n) -> System.out.println(c + ": " + n));

        System.out.println("-- Top 3 Packages --");
        reportService.topPackageStudents(3).forEach(System.out::println);

        System.out.printf("-- Overall Placement Percentage: %.2f%% --%n", reportService.overallPlacementPercentage());
    }


    private static Branch readBranch() {
        while (true) {
            System.out.print("Branch " + Arrays.toString(Branch.values()) + ": ");
            String input = sc.nextLine().trim().toUpperCase();
            try {
                return Branch.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid branch. Please enter one of: " + Arrays.toString(Branch.values()));
            }
        }
    }
    private static String readValidName(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (input.matches("[A-Za-z][A-Za-z .'-]*")) {
                return input;
            }
            System.out.println("Invalid name. Please enter letters only (no digits or symbols other than . ' -).");
        }
    }

    private static int readGraduationYear() {
        while (true) {
            System.out.print("Graduation Year (e.g. 2026): ");
            String input = sc.nextLine().trim();
            if (input.matches("\\d{4}")) {
                return Integer.parseInt(input);
            }
            System.out.println("Invalid graduation year. Please enter a positive 4-digit year (e.g. 2026).");
        }
    }

    private static double readValidCgpa() {
        while (true) {
            System.out.print("CGPA (0-10): ");
            String input = sc.nextLine().trim();
            try {
                double cgpa = Double.parseDouble(input);
                if (cgpa >= 0 && cgpa <= 10) {
                    return cgpa;
                }
            } catch (NumberFormatException ignored) {
                // fall through to error message below
            }
            System.out.println("Invalid CGPA. Please enter a number between 0 and 10.");
        }
    }

    private static int readNonNegativeInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // fall through to error message below
            }
            System.out.println("Invalid value. Please enter a whole number that is 0 or greater.");
        }
    }

    private static String readNonEmptyText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("This field cannot be empty. Please try again.");
        }
    }

    private static String readValidEmail(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (input.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
                return input;
            }
            System.out.println("Invalid email format. Please enter something like name@example.com.");
        }
    }

    private static double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // fall through to error message below
            }
            System.out.println("Invalid value. Please enter a number greater than 0.");
        }
    }

    private static double readCgpaInRange(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value >= 0 && value <= 10) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // fall through to error message below
            }
            System.out.println("Invalid CGPA. Please enter a number between 0 and 10.");
        }
    }

    private static LocalDate readValidDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return LocalDate.parse(input);
            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("Invalid date. Please use the format yyyy-MM-dd, e.g. 2026-12-01.");
            }
        }
    }

    private static Set<Branch> readBranchSet(String prompt) {
        while (true) {
            System.out.print(prompt);
            Set<String> tokens = parseCsv(sc.nextLine());
            if (tokens.isEmpty()) {
                return new HashSet<>();
            }
            Set<Branch> branches = new HashSet<>();
            boolean allValid = true;
            for (String token : tokens) {
                try {
                    branches.add(Branch.valueOf(token.trim().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid branch: '" + token + "'. Valid options are: " + Arrays.toString(Branch.values()));
                    allValid = false;
                    break;
                }
            }
            if (allValid) {
                return branches;
            }
        }
    }

    private static Set<Integer> readYearSet(String prompt) {
        while (true) {
            System.out.print(prompt);
            Set<String> tokens = parseCsv(sc.nextLine());
            if (tokens.isEmpty()) {
                return new HashSet<>();
            }
            Set<Integer> years = new HashSet<>();
            boolean allValid = true;
            for (String token : tokens) {
                String t = token.trim();
                if (t.matches("\\d{4}")) {
                    years.add(Integer.parseInt(t));
                } else {
                    System.out.println("Invalid year: '" + token + "'. Enter positive 4-digit years (e.g. 2026).");
                    allValid = false;
                    break;
                }
            }
            if (allValid) {
                return years;
            }
        }
    }

    private static Set<String> parseCsv(String line) {
        Set<String> result = new HashSet<>();
        if (line == null || line.trim().isEmpty()) return result;
        for (String part : line.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) result.add(trimmed);
        }
        return result;
    }

    private static Student pickStudent() {
        listStudents();
        while (true) {
            System.out.print("Enter Student ID (or press Enter to cancel): ");
            String id = sc.nextLine().trim();
            if (id.isEmpty()) { System.out.println("Cancelled."); return null; }
            Optional<Student> student = studentRepository.findById(id);
            if (student.isPresent()) return student.get();
            System.out.println("Invalid Student ID: '" + id + "'. Please try again.");
        }
    }

    private static Company pickCompany() {
        listCompanies();
        while (true) {
            System.out.print("Enter Company ID (or press Enter to cancel): ");
            String id = sc.nextLine().trim();
            if (id.isEmpty()) { System.out.println("Cancelled."); return null; }
            Optional<Company> company = companyRepository.findById(id);
            if (company.isPresent()) return company.get();
            System.out.println("Invalid Company ID: '" + id + "'. Please try again.");
        }
    }

    private static PlacementDrive pickDrive() {
        listDrives();
        while (true) {
            System.out.print("Enter Drive ID (or press Enter to cancel): ");
            String id = sc.nextLine().trim();
            if (id.isEmpty()) { System.out.println("Cancelled."); return null; }
            Optional<PlacementDrive> drive = driveRepository.findById(id);
            if (drive.isPresent()) return drive.get();
            System.out.println("Invalid Drive ID: '" + id + "'. Please try again.");
        }
    }

    private static Application pickApplication() {
        listApplications();
        while (true) {
            System.out.print("Enter Application ID (or press Enter to cancel): ");
            String id = sc.nextLine().trim();
            if (id.isEmpty()) { System.out.println("Cancelled."); return null; }
            Optional<Application> app = applicationRepository.findById(id);
            if (app.isPresent()) return app.get();
            System.out.println("Invalid Application ID: '" + id + "'. Please try again.");
        }
    }}

