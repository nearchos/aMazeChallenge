/*
 *  Copyright 2016 Google Inc. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.inspirecenter.amazechallenge.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.blockly.android.AbstractBlocklyActivity;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.blockly.model.DefaultBlocks;

import org.inspirecenter.amazechallenge.R;
import org.inspirecenter.amazechallenge.model.Maze;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BlocklyActivity extends AbstractBlocklyActivity {

    private static final String TAG = "MazeBlocklyActivity";
    private static final String SAVE_FILENAME = "maze_workspace.xml";
    private static final String AUTOSAVE_FILENAME = "maze_workspace_temp.xml";
    public static final String KEY_ALGORITHM_ACTIVITY_CODE = "KEY_ALGORITHM_ACTIVITY_CODE";

    private static final List<String> BLOCK_DEFINITIONS = Arrays.asList(
            DefaultBlocks.COLOR_BLOCKS_PATH,
            DefaultBlocks.LOGIC_BLOCKS_PATH,
            DefaultBlocks.LOOP_BLOCKS_PATH,
            DefaultBlocks.MATH_BLOCKS_PATH,
            DefaultBlocks.TEXT_BLOCKS_PATH,
            DefaultBlocks.VARIABLE_BLOCKS_PATH,
            "blocks/maze_blocks.json"
    );//end List BLOCK_DEFINITIONS

    private static final List<String> JAVASCRIPT_GENERATORS = Collections.singletonList(
            "generators/maze_blocks.js"
    );//end List JAVASCRIPT_GENERATORS

    CodeGenerationRequest.CodeGeneratorCallback mCodeGeneratorCallback =
            new CodeGenerationRequest.CodeGeneratorCallback() {
                @Override
                public void onFinishCodeGeneration(final String generatedCode) {
                    Log.i(TAG, "generatedCode:\n" + generatedCode);
                    Toast.makeText(getApplicationContext(), generatedCode,
                            Toast.LENGTH_LONG).show();
                    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BlocklyActivity.this);
                    sharedPreferences.edit().putString (KEY_ALGORITHM_ACTIVITY_CODE, generatedCode).apply();
                    finish();
                }
            };//end mCodeGeneratorCallback

    @Override
    protected View onCreateContentView(int containerId) {
        View root = getLayoutInflater().inflate(R.layout.activity_blockly, null);
        final Button applyButton = (Button) root.findViewById(R.id.applyButtonBlockly);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /** TODO, Code generation, if possible... Later! **/

                final Intent intent = getIntent();
                final Maze selectedMaze = (Maze) intent.getSerializableExtra(GameActivity.SELECTED_GAME_KEY);
                final Intent intentPlay = new Intent(BlocklyActivity.this, GameActivity.class);
                intentPlay.putExtra(GameActivity.SELECTED_GAME_KEY, selectedMaze);

                if (getController().getWorkspace().hasBlocks()) {
                    onRunCode();
                }
            }//end onClick()
        });//end listener
        return root;
    }//end onCreateContentView()

    @NonNull
    @Override
    protected List<String> getBlockDefinitionsJsonPaths() {
        return BLOCK_DEFINITIONS;
    }

    @NonNull
    @Override
    protected String getToolboxContentsXmlPath() {
        return "toolboxes/maze_toolbox.xml";
    }

    @NonNull
    @Override
    protected List<String> getGeneratorsJsPaths() {
        return JAVASCRIPT_GENERATORS;
    }

    @NonNull
    @Override
    protected CodeGenerationRequest.CodeGeneratorCallback getCodeGenerationCallback() {
        return mCodeGeneratorCallback;
    }

    @Override
    @NonNull
    protected String getWorkspaceSavePath() {
        return SAVE_FILENAME;
    }

    @Override
    @NonNull
    protected String getWorkspaceAutosavePath() {
        return AUTOSAVE_FILENAME;
    }

    @Override
    protected int getActionBarMenuResId() {
        return R.menu.blockly_action_bar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == com.google.blockly.android.R.id.action_clear) {
            onClearWorkspace();
            return true;
        } else if (id == android.R.id.home && mNavigationDrawer != null) {
            setNavDrawerOpened(!isNavDrawerOpen());
        }

        return super.onOptionsItemSelected(item);
    }//end onOptionsItemSelected

}//end activity BlocklyActivity
