Blockly.JavaScript['maze_move_forward'] = function(block) {
  var code = "__retVal = Packages.org.inspirecenter.amazechallenge.algorithms.PlayerMove.MOVE_FORWARD;\n" + //Change return value to MOVE_FORWARD
  "propNames.forEach(function(item, index) { instance.setJavascriptArgument(item, this[item]); } );\n" + //Save variable to map before returning
  "return __retVal;\n"; //return to Java
  return code;
};

Blockly.JavaScript['maze_turn_cw'] = function(block) {
  var code = "__retVal = Packages.org.inspirecenter.amazechallenge.algorithms.PlayerMove.TURN_CLOCKWISE;\n" + //Change return value to TURN_CLOCKWISE
    "propNames.forEach(function(item, index) { instance.setJavascriptArgument(item, this[item]); } );\n" + //Save variable to map before returning
    "return __retVal;\n"; //return to Java
  return code;
};

Blockly.JavaScript['maze_turn_ccw'] = function(block) {
    var code = "__retVal = Packages.org.inspirecenter.amazechallenge.algorithms.PlayerMove.TURN_COUNTERCLOCKWISE;\n" + //Change return value to TURN_COUNTERCLOCKWISE
      "propNames.forEach(function(item, index) { instance.setJavascriptArgument(item, this[item]); } );\n" + //Save variable to map before returning
      "return __retVal;\n"; //return to Java
    return code;
};

Blockly.JavaScript['maze_obstacle_exists'] = function(block) {
  var value_direction = Blockly.JavaScript.valueToCode(block, 'DIRECTION', Blockly.JavaScript.ORDER_ATOMIC);
  // TODO: Assemble JavaScript into code variable.
  var code = '...';
  // TODO: Change ORDER_NONE to the correct strength.
  return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['maze_canmove_left'] = function(block) {
  var code = 'instance.canMoveLeft()';
  // TODO: Change ORDER_NONE to the correct strength.
  return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['maze_canmove_right'] = function(block) {
    var code = 'instance.canMoveRight()';
  // TODO: Change ORDER_NONE to the correct strength.
  return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['maze_canmove_forward'] = function(block) {
    var code = 'instance.canMoveForward()\n';
  // TODO: Change ORDER_NONE to the correct strength.
  return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['maze_canmove_backward'] = function(block) {
    var code = 'instance.canMoveBackward()\n';
  // TODO: Change ORDER_NONE to the correct strength.
  return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['maze_direction_north'] = function(block) {
  var code = 'Packages.org.inspirecenter.amazechallenge.model.Direction.NORTH';
  // TODO: Change ORDER_NONE to the correct strength.
  return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['maze_direction_south'] = function(block) {
  var code = 'Packages.org.inspirecenter.amazechallenge.model.Direction.SOUTH';
  // TODO: Change ORDER_NONE to the correct strength.
  return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['maze_direction_east'] = function(block) {
  var code = 'Packages.org.inspirecenter.amazechallenge.model.Direction.EAST';
  // TODO: Change ORDER_NONE to the correct strength.
  return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['maze_direction_west'] = function(block) {
  var code = 'Packages.org.inspirecenter.amazechallenge.model.Direction.WEST';
  // TODO: Change ORDER_NONE to the correct strength.
  return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['maze_init_function'] = function(block) {
  var statements_init = Blockly.JavaScript.statementToCode(block, 'init');
  var code = "function init(instance) {\n"
   + statements_init
   + "  propNames.forEach(function(item, index) { instance.setJavascriptArgument(item, this[item]); } );\n"
   + "}//end init()\n\n";
  return code;
};

Blockly.JavaScript['maze_run_function'] = function(block) {
  var statements_run = Blockly.JavaScript.statementToCode(block, 'run');
  var code = "function wrapper(instance) {\n" +
  //Default return value:
  "  var __retVal = Packages.org.inspirecenter.amazechallenge.algorithms.PlayerMove.NO_MOVE;\n" +
  //Get the values of the variables from the map:
  "  propNames.forEach(function(item, index) {\n" +
  "      var mapValue = instance.getJavascriptArgument(item);\n" +
  //Check if this is a boolean literal
  "      if (mapValue == 'false') this[item] = false;\n" +
  "      else if (mapValue == 'true') this[item] = true;\n" +
  //Check if this is a number literal
  "      else if (!isNaN(mapValue)) this[item] = Number(mapValue); \n" +
  //Check if this is a direction literal
  "      else if (mapValue == 'north') this[item] = Packages.org.inspirecenter.amazechallenge.model.Direction.NORTH;\n" +
  "      else if (mapValue == 'south') this[item] = Packages.org.inspirecenter.amazechallenge.model.Direction.SOUTH;\n" +
  "      else if (mapValue == 'west') this[item] = Packages.org.inspirecenter.amazechallenge.model.Direction.WEST;\n" +
  "      else if (mapValue == 'east') this[item] = Packages.org.inspirecenter.amazechallenge.model.Direction.EAST;\n" +
  //Otherwise it's a string/character literal
  "  });\n" +
  //Include the player's code:
  "\n\n//---- PLAYER'S CODE ----\n\n" + statements_run + "\n\n//-----------------------\n\n" +
  //Save all of the values back to the map:
  "  propNames.forEach(function(item, index) { instance.setJavascriptArgument(item, this[item]); } );\n" +
  //If the code does not return yet, return the default value (NO_MOVE).
  "  return __retVal;\n" +

  "}//end wrapper()\n";
  return code;
};