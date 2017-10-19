Blockly.JavaScript['maze_move_forward'] = function(block) {
  var code = 'retVal = Packages.org.inspirecenter.amazechallenge.algorithms.PlayerMove.MOVE_FORWARD; propNames.forEach(function(item, index) { this[item] = instance.getJavascriptArgument(item); } ); return results;\n';
  return code;
};

Blockly.JavaScript['maze_turn_cw'] = function(block) {
  var code = 'retVal = Packages.org.inspirecenter.amazechallenge.algorithms.PlayerMove.TURN_CLOCKWISE; propNames.forEach(function(item, index) { this[item] = instance.getJavascriptArgument(item); } ); return results;\n';
  return code;
};

Blockly.JavaScript['maze_turn_ccw'] = function(block) {
  var code = 'retVal = Packages.org.inspirecenter.amazechallenge.algorithms.PlayerMove.TURN_COUNTERCLOCKWISE; propNames.forEach(function(item, index) { this[item] = instance.getJavascriptArgument(item); } );  return results;\n';
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
  var code = 'Packages.org.inspirecenter.amazechallenge.model.Direction.NORTH;\n';
  // TODO: Change ORDER_NONE to the correct strength.
  return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['maze_direction_south'] = function(block) {
  var code = 'Packages.org.inspirecenter.amazechallenge.model.Direction.SOUTH;\n';
  // TODO: Change ORDER_NONE to the correct strength.
  return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['maze_direction_east'] = function(block) {
  var code = 'Packages.org.inspirecenter.amazechallenge.model.Direction.EAST;\n';
  // TODO: Change ORDER_NONE to the correct strength.
  return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['maze_direction_west'] = function(block) {
  var code = 'Packages.org.inspirecenter.amazechallenge.model.Direction.WEST;\n';
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
  "  propNames.forEach(function(item, index) { this[item] = instance.getJavascriptArgument(item); } );\n" +
  "\n\n//---- PLAYER'S CODE ----\n\n" + statements_run + "\n\n//-----------------------\n\n" +
  "  propNames.forEach(function(item, index) { instance.setJavascriptArgument(item, this[item]); } );\n" +
  "}\n";
  return code;
};