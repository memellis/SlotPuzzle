/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle.level.creator.utils.box2d;

import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.utils.InputMatrix;

public class SlotPuzzleMatrices {

    public static int[][] createMatrixMimickingDynamicMatrixFailed1() {
        String matrixToInput = "12 x 9\n"
            + "-1  0 -1  0 -1 -1 -1 -1 -1  0 -1  0\n"
            + "-1 -1  0  0 -1 -1  0 -1 -1 -1  0  0\n"
            + "-1 -1 -1 -1  0 -1  0 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1  0  0  0 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixMimickingDynamicMatrixFailed2() {
        String matrixToInput = "12 x 9\n"
            + " 0  0 -1  0 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1  0  0 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0  0  0 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixMimickingDynamicMatrixFailed3() {
        String matrixToInput = "12 x 9\n"
            + "-1  0 -1  0 -1  0 -1  0 -1  0 -1  0\n"
            + "-1  0  0  0 -1 -1  0  0 -1 -1  0  0\n"
            + "-1  0 -1 -1  0  0  0  0 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1  0  0  0  0\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixMimickingDynamicMatrix() {
        String matrixToInput = "12 x 9\n"
            + "-1  0 -1  0 -1  0 -1  0 -1  0 -1  0\n"
            + "-1 -1  0  0 -1 -1  0  0 -1 -1  0  0\n"
            + "-1 -1 -1 -1  0  0  0  0 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1  0  0  0  0\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithNoBoxes() {
        String matrixToInput = "12 x 9\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithOneBox() {
        String matrixToInput = "12 x 9\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithTwoBoxes() {
        String matrixToInput = "12 x 9\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithThreeBoxes() {
        String matrixToInput = "12 x 9\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithFourBoxes() {
        String matrixToInput = "12 x 9\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithFillColumnNineBoxes() {
        String matrixToInput = "12 x 9\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithOneBoxOnOneBox() {
        String matrixToInput = "12 x 9\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithTwoBoxesOnOneBox() {
        String matrixToInput = "12 x 9\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithThreeBoxesOnOneBox() {
        String matrixToInput = "12 x 9\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithTwoByOnOneBox() {
        String matrixToInput = "12 x 9\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
            + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithTwoByOnTwoBoxes() {
        String matrixToInput = "12 x 9\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithTwoByOnThreeBoxes() {
        String matrixToInput = "12 x 9\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithTwoByOnTFourBoxes() {
        String matrixToInput = "12 x 9\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0 -0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithTwoFillColumnNineBoxes() {
        String matrixToInput = "12 x 9\n"
            + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithOneBoxGapTwoOnOneBox() {
        String matrixToInput = "12 x 9\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithOneBoxGapThreeOnOneBox() {
        String matrixToInput = "12 x 9\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithAFullMatrix() {
        String matrixToInput = "12 x 9\n"
            + " 0  0  0  0  0  0  0  0  0  0   0   0\n"
            + " 0  0  0  0  0  0  0  0  0  0   0   0\n"
            + " 0  0  0  0  0  0  0  0  0  0   0   0\n"
            + " 0  0  0  0  0  0  0  0  0  0   0   0\n"
            + " 0  0  0  0  0  0  0  0  0  0   0   0\n"
            + " 0  0  0  0  0  0  0  0  0  0   0   0\n"
            + " 0  0  0  0  0  0  0  0  0  0   0   0\n"
            + " 0  0  0  0  0  0  0  0  0  0   0   0\n"
            + " 0  0  0  0  0  0  0  0  0  0   0   0\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithANearlyFullMatrix() {
        String matrixToInput = "12 x 9\n"
            + " 0  1  2  3  4 -1 -1 -1 -1 -1 -1 -1\n"
            + " 7  0  1  2  3  4  5  6  7  0  1  2\n"
            + " 6  7  0  1  2  3  4  5  6  0  0  1\n"
            + " 5  6  7  0  1  2  3  4  5  6  7  0\n"
            + " 4  5  6  7  0  1  2  3  4  5  6  7\n"
            + " 3  4  5  6  7  0  1  2  3  4  5  6\n"
            + " 2  3  4  5  6  7  0  1  2  3  4  5\n"
            + " 1  2  3  4  5  6  7  0  1  2  3  4\n"
            + " 0  1  2  3  4  5  6  7  0  1  2  3\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithOneBomb() {
        String matrixToInput = "12 x 9\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1  9 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithTwoBombs() {
        String matrixToInput = "12 x 9\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1  9  9 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithTwoBombsSurroundedByReelTilesTopLeft() {
        String matrixToInput = "12 x 9\n"
            + " 0  1  2  3 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 1  8  8  4 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0  7  6  5 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createMatrixWithTwoBombsSurroundedBySomeReelTiles() {
        String matrixToInput = "12 x 9\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + " 0  7  6  5  2  4  1  2  3  7   1  -1\n"
            + " 4  2  3  0  1  2  3  4  6  0   3  -1\n"
            + " 2  1  5  1  8  8  4  0  1  2   5  -1\n"
            + " 3  4  6  7  6  5  2  5  3  4   0  -1\n"
            + " 2  5  7  6  3  2  4  6  4  1   2  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
            + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        return
            new InputMatrix(matrixToInput).readMatrix();
    }

    public static int[][] createDynamicMatrix(int[] matrixIdentifier,
                                              int width,
                                              int height) {
        int[][] dynamicMatrix = new int[height][width];

        for (int r = 0; r < dynamicMatrix.length; r++)
            for (int c = 0; c < dynamicMatrix[0].length; c++) {
                dynamicMatrix[r][c] =  extractBitAt(matrixIdentifier[c], r) == 0 ? -1 : 0;
            }

        return dynamicMatrix;
    }

    public static int extractBitAt(int number, int position) {
        return (number >> position) & 1;
    }

    public static Array<int[][]> getSlotMatrices() {
        Array<int[][]> slotMatrices = new Array<>();
        slotMatrices.add(createMatrixMimickingDynamicMatrixFailed1());
        slotMatrices.add(createMatrixMimickingDynamicMatrixFailed2());
        slotMatrices.add(createMatrixMimickingDynamicMatrixFailed3());
        slotMatrices.add(createMatrixMimickingDynamicMatrix());
        slotMatrices.add(createMatrixWithNoBoxes());
        slotMatrices.add(createMatrixWithOneBox());
        slotMatrices.add(createMatrixWithOneBoxGapThreeOnOneBox());
        slotMatrices.add(createMatrixWithOneBoxGapTwoOnOneBox());
        slotMatrices.add(createMatrixWithFillColumnNineBoxes());
        slotMatrices.add(createMatrixWithThreeBoxesOnOneBox());
        slotMatrices.add(createMatrixWithThreeBoxesOnOneBox());
        slotMatrices.add(createMatrixWithThreeBoxesOnOneBox());
        slotMatrices.add(createMatrixWithOneBoxOnOneBox());
        slotMatrices.add(createMatrixWithTwoBoxesOnOneBox());
        slotMatrices.add(createMatrixWithThreeBoxesOnOneBox());
        slotMatrices.add(createMatrixWithOneBoxOnOneBox());
        slotMatrices.add(createMatrixWithTwoBoxesOnOneBox());
        slotMatrices.add(createMatrixWithThreeBoxesOnOneBox());
        slotMatrices.add(createMatrixWithTwoByOnOneBox());
        slotMatrices.add(createMatrixWithTwoByOnTwoBoxes());
        slotMatrices.add(createMatrixWithTwoByOnThreeBoxes());
        slotMatrices.add(createMatrixWithTwoByOnTFourBoxes());
        slotMatrices.add(createMatrixWithTwoFillColumnNineBoxes());
        slotMatrices.add(createMatrixWithOneBox());
        slotMatrices.add(createMatrixWithTwoBoxes());
        slotMatrices.add(createMatrixWithThreeBoxes());
        slotMatrices.add(createMatrixWithFillColumnNineBoxes());
        return slotMatrices;
    }

    public static Array<int[][]> getSlotMatricesTouchingBottom() {
        Array<int[][]> slotMatrices = new Array<>();
        slotMatrices.add(createMatrixWithOneBox());
        slotMatrices.add(createMatrixWithTwoBoxes());
        slotMatrices.add(createMatrixWithThreeBoxes());
        slotMatrices.add(createMatrixWithFourBoxes());
        slotMatrices.add(createMatrixWithTwoByOnOneBox());
        slotMatrices.add(createMatrixWithTwoByOnTwoBoxes());
        slotMatrices.add(createMatrixWithTwoByOnThreeBoxes());
        slotMatrices.add(createMatrixWithTwoByOnTFourBoxes());
        slotMatrices.add(createMatrixWithTwoFillColumnNineBoxes());
        return slotMatrices;
    }
}
