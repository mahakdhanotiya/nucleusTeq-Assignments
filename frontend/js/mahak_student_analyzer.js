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


// function to calculate total marks 
function calculateTotal(student) {
  let total = 0;

  for (let i = 0; i < student.marks.length; i++) {
    total += student.marks[i].score;
  }

  return total;
}
//Print result 
students.forEach(function(student) {
  let totalMarks = calculateTotal(student);
  console.log(student.name + " Total Marks: " + totalMarks);
});


// function to calculate average marks
function calculateAverage(student) {
  let total = 0;

  for (let i = 0; i < student.marks.length; i++) {
    total += student.marks[i].score;
  }

  let average = total / student.marks.length;

  return average;
}
// print average marks
students.forEach(function(student) {
  let avg = calculateAverage(student);
  console.log(student.name + " Average Marks: " + avg);
});


//SUBJECT-WISE HIGHEST SCORES

// list of subjects
let subjects = ["Math", "English", "Science", "History", "Computer"];

subjects.forEach(function(sub) {
  let highestScore = 0;
  let topperName = "";

  students.forEach(function(student) {
    student.marks.forEach(function(mark) {

      // check if subject matches and score is highest
      if (mark.subject === sub && mark.score > highestScore) {
        highestScore = mark.score;
        topperName = student.name;
      }

    });
  });

  console.log("Highest in " + sub + ": " + topperName + " (" + highestScore + ")");
});


// SUBJECT-WISE AVERAGE

let subjects = ["Math", "English", "Science", "History", "Computer"];

subjects.forEach(function(sub) {
  let total = 0;
  let count = 0;

  // loop through all students
  students.forEach(function(student) {

    // check marks for current subject
    student.marks.forEach(function(mark) {

      if (mark.subject === sub) {
        total += mark.score;
        count++;
      }

    });
  });

  let average = total / count; // calculate average

  console.log("Average " + sub + " Score: " + average.toFixed(2));
});

