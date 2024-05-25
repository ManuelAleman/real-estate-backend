const mongoose = require("mongoose");

const userSchema = new mongoose.Schema(
  {
    name: {
      type: String,
      required: [true, "Name is required"],
    },
    contactNumber: {
      type: String,
      required: [true, "Contact Number is required"],
    },
    email: {
      type: String,
      required: [true, "Email is required"],
      unique: true,
    },
    password: {
      type: String,
      required: [true, "Password is required"],
    },
    role: {
      type: String,
      enum: ["user", "seller", "admin"],
      default: "user",
    },
    profilePicture: {
      type: String,
      default:
        "https://w7.pngwing.com/pngs/178/595/png-transparent-user-profile-computer-icons-login-user-avatars-thumbnail.png",
    },
    answer: {
      type: String,
      require: [true, "Answer is required"],
    },
  },
  { timestamps: true }
);

module.exports = mongoose.model("User", userSchema);
