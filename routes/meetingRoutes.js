const express = require("express");
const authMiddleware = require("../middleware/authMiddleware");
const {
  createMeetingController,
  getAllMeetingsFromUser,
  getMyMeetingInfo,
  getMeetingsWhereImSeller,
  updateMeetigStatus,
} = require("../controllers/meetingController");

const router = express.Router();

router.post("/createMeeting", authMiddleware, createMeetingController);

router.get("/getAllMeetingsFromUser", authMiddleware, getAllMeetingsFromUser);

router.get("/getMyMeetingInfo/:id", authMiddleware, getMyMeetingInfo);

router.get(
  "/getMeetingsWhereImSeller/:id",
  authMiddleware,
  getMeetingsWhereImSeller
);

router.put("/updateMeetigStatus/:id", authMiddleware, updateMeetigStatus);

module.exports = router;
