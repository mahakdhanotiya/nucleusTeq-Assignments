// Student data
const students = [
{
 name: "Lalit",
 marks: [
   { subject: "Math", score: 78 },
   { subject: "English", score: 82 },
   { subject: "Science", score: 74 },
   { subject: "History", score: 69 },
   { subject: "Computer", score: 88 }
 ],
 attendance: 82
},
{
 name: "Rahul",
 marks: [
   { subject: "Math", score: 90 },
   { subject: "English", score: 85 },
   { subject: "Science", score: 80 },
   { subject: "History", score: 76 },
   { subject: "Computer", score: 92 }
 ],
 attendance: 91
}
];


// function to calculate total marks of a student
function calculateTotal(student) {
  let total = 0;

  // loop through each subject
  for (let i = 0; i < student.marks.length; i++) {
    total += student.marks[i].score;
  }

  return total;
}
//Print total marks
students.forEach(function(student) {
  let totalMarks = calculateTotal(student);
  console.log(student.name + " Total Marks: " + totalMarks);
});