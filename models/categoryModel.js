const mongoose = require("mongoose");

const categorySchema = new mongoose.Schema(
  {
    name: {
      type: String,
      required: [true, "Name is required"],
    },
    description: {
      type: String,
      required: [true, "Description is required"],
    },
    image: {
      type: String,
      default:
        "https://w7.pngwing.com/pngs/395/812/png-transparent-project-management-computer-icons-business-categories-blue-angle-rectangle.png",
    },
  },
  { timestamps: true }
);

module.exports = mongoose.model("Category", categorySchema);
