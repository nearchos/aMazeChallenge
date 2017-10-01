Blockly.JavaScript['maze_move_forward'] = function(block) {
  var code = 'var retVal = Packages.org.inspirecenter.amazechallenge.algorithms.PlayerMove.MOVE_FORWARD; return new Packages.org.inspirecenter.amazechallenge.model.InterpretedMazeRunnerParams(retVal, justTurned);\n';
  return code;
};

Blockly.JavaScript['maze_turn_cw'] = function(block) {
  var code = 'var retVal = Packages.org.inspirecenter.amazechallenge.algorithms.PlayerMove.TURN_CLOCKWISE; return new Packages.org.inspirecenter.amazechallenge.model.InterpretedMazeRunnerParams(retVal, justTurned);\n';
  return code;
};

Blockly.JavaScript['maze_turn_ccw'] = function(block) {
  var code = 'var retVal = Packages.org.inspirecenter.amazechallenge.algorithms.PlayerMove.TURN_COUNTERCLOCKWISE; return new Packages.org.inspirecenter.amazechallenge.model.InterpretedMazeRunnerParams(retVal, justTurned);\n';
  return code;
};

Blockly.JavaScript['maze_obstacle_exists'] = function(block) {
  var value_direction = Blockly.JavaScript.valueToCode(block, 'DIRECTION', Blockly.JavaScript.ORDER_ATOMIC);
  // TODO: Assemble JavaScript into code variable.
  var code = '...';
  // TODO: Change ORDER_NONE to the correct strength.
  return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['maze_is_passable'] = function(block) {
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

Blockly.JavaScript['maze_justturned'] = function(block) {
  var code = 'getJustTurned()';
  // TODO: Change ORDER_NONE to the correct strength.
  return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['maze_set_justturned'] = function(block) {
  var value_justturned = Blockly.JavaScript.valueToCode(block, 'justTurned', Blockly.JavaScript.ORDER_ATOMIC);
  var code = 'justTurned = ' + value_justturned + ';\n';
  return code;
};

Blockly.JavaScript['maze_getrandomnumber'] = function(block) {
  var value_low = Blockly.JavaScript.valueToCode(block, 'low', Blockly.JavaScript.ORDER_ATOMIC);
  var value_high = Blockly.JavaScript.valueToCode(block, 'high', Blockly.JavaScript.ORDER_ATOMIC);
  // TODO: Assemble JavaScript into code variable.
  var code = '...';
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