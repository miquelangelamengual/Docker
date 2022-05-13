const express = require('express');
const router = express.Router();
const daw = require('../services/daw');

/* GET programming languages. */
router.get('/:q', async function(req, res, next) {
  try {
    console.log("Paràmetre q:",req.params.q)
    res.json(await daw.getMultiple(req.params.q));
  } catch (err) {
    console.error(`Error on Database `, err.message);
    next(err);
  }
});

module.exports = router;