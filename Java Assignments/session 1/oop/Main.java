public class Main {
    public static void main(String[] args) {

        Student s1 = new Student();

        s1.setName("Mahak");
        s1.setRollNumber(101);
        s1.setMarks(88.5);

        s1.display("Student Details:");

        GraduateStudent g1 = new GraduateStudent();

        g1.setName("Riya");
        g1.setRollNumber(102);
        g1.setMarks(90.5);
        g1.specialization = "Computer Science";
         
        g1.calculateGrade();
        g1.display();
    }
}