// STUDENT DATA
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


//TOTAL MARKS

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


//AVERAGE MARKS

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



// OVERALL CLASS TOPPER

let topperName = "";
let highestMarks = 0;

students.forEach(function(student) {
  let total = 0;

  // calculate total marks of this student
  student.marks.forEach(function(mark) {
    total += mark.score;
  });

  // check if current student has highest marks
  if (total > highestMarks) {
    highestMarks = total;
    topperName = student.name;
  }
});

console.log("Class Topper: " + topperName + " with " + highestMarks + " marks");


//Adding test data to original data

const studentsTest = [
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
  },
  {
    name: "Aman",
    marks: [
      { subject: "Math", score: 35 }, 
      { subject: "English", score: 60 },
      { subject: "Science", score: 55 },
      { subject: "History", score: 50 },
      { subject: "Computer", score: 65 }
    ],
    attendance: 80
  },
  {
    name: "Riya",
    marks: [
      { subject: "Math", score: 88 },
      { subject: "English", score: 92 },
      { subject: "Science", score: 90 },
      { subject: "History", score: 85 },
      { subject: "Computer", score: 91 }
    ],
    attendance: 60  
  }
];


// FAIL CONDITION CHECK


studentsTest.forEach(function(student) {
  let hasFailed = false;
  let failReason = "";

  // checking subject marks
  student.marks.forEach(function(mark) {
    if (mark.score <= 40) {
      hasFailed = true;
      failReason = "Failed in " + mark.subject;
    }
  });

  // checking attendance
  if (student.attendance < 75) {
    hasFailed = true;
    failReason = "Low Attendance";
  }

  if (hasFailed) {
    console.log(student.name + " Grade: Fail (" + failReason + ")");
  }
});


// FINAL GRADE CALCULATION


studentsTest.forEach(function(student) {
  let total = 0;
  let hasFailed = false;
  let failReason = "";

  // calculate total marks and check subject fail
  student.marks.forEach(function(mark) {
    total += mark.score;

    if (mark.score <= 40) {
      hasFailed = true;
      failReason = "Failed in " + mark.subject;
    }
  });

  let average = total / student.marks.length;

  // checking attendance condition
  if (student.attendance < 75) {
    hasFailed = true;
    failReason = "Low Attendance";
  }

  if (hasFailed) {
    console.log(student.name + " Grade: Fail (" + failReason + ")");
  } else {
    let grade = "";

    // assign grade based on average
    if (average >= 85) grade = "A";
    else if (average >= 70) grade = "B";
    else if (average >= 50) grade = "C";
    else grade = "D";

    console.log(student.name + " Grade: " + grade);
  }
});