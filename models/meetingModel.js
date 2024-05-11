const mongoose = require("mongoose");

const meetingSchema = new mongoose.Schema(
  {
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: [true, "User is required"],
    },
    estate: {
      type: mongoose.Schema.Types.ObjectId,
      required: [true, "Estate is required"],
    },
    date: {
      type: Date,
      required: [true, "Date is required"],
    },
  },
  { timestamps: true }
);

module.exports = mongoose.model("Meeting", meetingSchema);