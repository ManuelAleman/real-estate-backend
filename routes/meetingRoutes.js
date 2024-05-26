const express = require("express");
const authMiddleware = require("../middleware/authMiddleware");
const {
  createMeetingController,
  getAllMeetingsFromUser,
  getMyMeetingInfo,
} = require("../controllers/meetingController");

const router = express.Router();

router.post("/createMeeting", authMiddleware, createMeetingController);

router.get("/getAllMeetingsFromUser", authMiddleware, getAllMeetingsFromUser);

router.get("/getMyMeetingInfo/:id", authMiddleware, getMyMeetingInfo);

module.exports = router;
