const estateModel = require("../models/estateModel");
const sellerModel = require("../models/sellerModel");
const userModel = require("../models/userModel");
const meetingModel = require("../models/meetingModel");

//CREATE MEETING REQUEST
const createMeetingController = async (req, res) => {
  try {
    const { estate_id, date, message } = req.body;

    if (!estate_id || !date || !message) {
      return res.status(400).send({
        success: false,
        message: "Please fill all the fields",
      });
    }

    const user_id = req.body.id;
    const estate = await estateModel.findById(estate_id);

    if (!estate) {
      return res.status(404).send({
        success: false,
        message: "Estate not found",
      });
    }

    console.log(estate.user._id.toString());
    console.log(user_id);
    if (estate.user._id.toString() === user_id) {
      return res.status(400).send({
        success: false,
        message: "No puedes hacerte una cita tu solo",
      });
    }

    const seller = req.body.seller || estate.seller;

    const waiting = seller == null ? true : false;

    const validDate = new Date(date);
    const currentDate = new Date();

    if (validDate < currentDate) {
      return res.status(400).send({
        success: false,
        message: "Ingresa una fecha valida",
      });
    }

    const meeting = await meetingModel.findOne({ estate: estate_id, date });
    if (meeting) {
      return res.status(500).send({
        success: false,
        message:
          "Ya hay una reunion programada para esta fecha en esta propiedad",
      });
    }

    const newMeeting = new meetingModel({
      estate: estate_id,
      user: user_id,
      date,
      message,
      seller: seller,
      waitingSeller: waiting,
    });

    await newMeeting.save();
    res.status(200).send({
      success: true,
      message: "Meeting Request Created",
      user: user_id,
      estate: estate_id,
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error",
      error,
    });
  }
};

const getAllMeetingsFromUser = async (req, res) => {
  try {
    const user_id = req.params.id;
    const meetings = await meetingModel.find({ user: user_id });

    if (meetings.length === 0) {
      return res.status(404).send({
        success: false,
        message: "No meetings found",
      });
    }

    res.status(200).send({
      success: true,
      message: "Meetings found",
      meetings,
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error",
      error,
    });
  }
};

const getMyMeetingInfo = async (req, res) => {
  try {
    const user_id = req.params.id;
    const meetings = await meetingModel
      .find({ user: user_id })
      .populate("estate")
      .populate({ path: "seller", populate: { path: "user" } })
      .populate("user")
      .exec();

    if (meetings.length === 0) {
      return res.status(404).send({
        success: false,
        message: "No meetings found",
      });
    }

    res.status(200).send({
      success: true,
      message: "Meetings found",
      meetings,
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error",
      error,
    });
  }
};

const getMeetingsWhereImSeller = async (req, res) => {
  try {
    const seller_id = req.params.id;
    const meetings = await meetingModel
      .find({
        seller: seller_id,
        waitingSeller: false,
      })
      .populate("estate")
      .populate({ path: "seller", populate: { path: "user" } })
      .populate("user")
      .exec();

    if (meetings.length === 0) {
      return res.status(404).send({
        success: false,
        message: "No meetings found",
      });
    }

    res.status(200).send({
      success: true,
      message: "Meetings found",
      meetings,
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error",
      error,
    });
  }
};

const updateMeetigStatus = async (req, res) => {
  try {
    const meeting_id = req.params.id;
    const status = req.body.status;
    const meeting = await meetingModel.findById(meeting_id);
    if (!meeting) {
      return res.status(404).send({
        success: false,
        message: "Meeting not found",
      });
    }

    meeting.status = status;
    await meeting.save();

    res.status(200).send({
      success: true,
      message: "Meeting updated",
      meeting,
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error",
      error,
    });
  }
};

const getMeetingWithNoSeller = async (req, res) => {
  try {
    const meetings = await meetingModel
      .find({ waitingSeller: true })
      .populate("estate")
      .populate({ path: "seller", populate: { path: "user" } })
      .populate("user")
      .exec();

    if (meetings.length === 0) {
      return res.status(404).send({
        success: false,
        message: "No meetings found",
      });
    }

    res.status(200).send({
      success: true,
      message: "Meetings found",
      meetings,
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error",
      error,
    });
  }
};

const addSellerToMeeting = async (req, res) => {
  try {
    const meeting_id = req.params.id;
    const seller_id = req.body.idSeller;
    const meeting = await meetingModel.findById(meeting_id);

    if (!meeting) {
      return res.status(404).send({
        success: false,
        message: "Meeting not found",
      });
    }

    meeting.seller = seller_id;
    meeting.waitingSeller = false;
    await meeting.save();

    res.status(200).send({
      success: true,
      message: "Seller added to meeting",
      meeting,
    });
  } catch (error) {
    console.log(error);
    res.status(500).send({
      success: false,
      message: "Internal Server Error",
      error,
    });
  }
};

module.exports = {
  createMeetingController,
  getAllMeetingsFromUser,
  getMyMeetingInfo,
  getMeetingsWhereImSeller,
  updateMeetigStatus,
  getMeetingWithNoSeller,
  addSellerToMeeting,
};
