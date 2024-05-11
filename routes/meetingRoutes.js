const express = require("express");
const authMiddleware = require("../middleware/authMiddleware");
const { createMeetingController } = require("../controllers/meetingController");

const router = express.Router();

router.post("/createMeeting", authMiddleware, createMeetingController);

module.exports = router;
