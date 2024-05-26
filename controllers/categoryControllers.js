const categoryModel = require("../models/categoryModel");

const addCategoryController = async (req, res) => {
  try {
    const { name, description, image } = req.body;
    //validate
    if (!name || !description) {
      return res.status(500).send({
        success: false,
        message: "Please fill all the fields",
      });
    }
    const newCategory = new categoryModel({ name, description, image });
    await newCategory.save();
    res.status(200).send({
      success: true,
      message: "Category added successfully",
      newCategory,
    });
  } catch (error) {
    console.log("error in API");
    return res.status(500).send({
      success: false,
      message: "Internal Server Error in add category api",
    });
  }
};

//GET ALL CATEGORY
const getAllCategoryController = async (req, res) => {
  try {
    const categories = await categoryModel.find({});
    if (!categories) {
      return res.status(404).send({
        success: false,
        message: "No category found",
      });
    }

    res.status(200).send({
      success: true,
      message: "All categories",
      categories,
    });
  } catch (error) {
    console.log("error in get all cat API");
    res.status(500).send({
      success: false,
      message: "Internal Server Error in get all category api",
    });
  }
};

const getCategoryByIdController = async (req, res) => {
  try {
    const category = await categoryModel.findById(req.params.id);
    if (!category) {
      return res.status(404).send({
        success: false,
        message: "No category found",
      });
    }

    res.status(200).send({
      success: true,
      message: "Category",
      category,
    });
  } catch (error) {
    console.log("error in get cat by id API");
    res.status(500).send({
      success: false,
      message: "Internal Server Error in get category by id api",
    });
  }
};

module.exports = {
  addCategoryController,
  getAllCategoryController,
  getCategoryByIdController,
};
