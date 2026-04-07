public class GraduateStudent extends Student {

    String specialization;

    char grade;

    void calculateGrade() {
       if (getMarks()>= 90) {
           grade = 'A';
       } else if (getMarks() >= 75) {
           grade = 'B';
       } else {
           grade = 'C';
       }
    }

    @Override
    void display() {
       super.display();  
       System.out.println("Specialization: " + specialization);
       System.out.println("Grade: " + grade);
    }
}

    

