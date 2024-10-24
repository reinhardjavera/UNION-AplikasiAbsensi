## UTS - IF570BL - Mobile Application

- Reinhard Javera Maheswara (00000077732) - [@reinhardjavera](https://github.com/reinhardjavera)

# UNION - IF570EL (Lab)

UNION is an application that allows users to perform attendance using photos and store data in Firebase. This application was created for the Midterm Exam of the IF570 Mobile Application course.

## Key Features

1. **Home Page:** Displays the current date and time, along with a button to perform attendance. Users can take a photo as proof of attendance.
2. **Camera for Attendance:** Enables users to access the camera and take a photo during attendance. They can delete or retake the photo before uploading it.
3. **Attendance Restrictions:**
   - Users can check in once and check out once per day.
   - After checking in, users cannot check in again on the same day.
   - Once users have checked out, they must wait until the next day to check in again.
4. **Attendance History:** Lists all attendance records with the most recent entries at the top, while older records appear below.
5. **Profile Page:** Users can enter their name and student ID (NIM), and save this information to Firebase.

## Application Demo

Link Video: (https://youtu.be/ywkrqsVhKAQ)

## Installation

1. Clone this repository:

```bash
git clone https://github.com/reinhardjavera/UNION.git
```

2. Open the project in Android Studio.
3. Make sure to add Firebase to your project and configure the `google-services.json` file.
4. Run the application on an emulator or Android device.
   
## Technologies Used

- **Android SDK:** For developing the Android application.
- **Firebase:** For data storage and user authentication.
- **RecyclerView:** For displaying attendance history.
- **Glide:** For loading and caching images efficiently in the app.
  
## References

- [Android Developer Codelabs](https://developer.android.com/get-started/codelabs)
- [ChatGPT](https://chat.openai.com/)
- [GitHub Copilot](https://github.com/features/copilot)
