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

package com.ellzone.slotpuzzle.utils;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputMatrix {
    private final String matrixToInput;
    private Scanner scanner;

    public InputMatrix(String matrixToInput) {
        this.matrixToInput = matrixToInput;
        scanner = new Scanner(matrixToInput);
    }

    public int[][] readMatrix() throws InputMismatchException {
        int matrixWidth = scanner.nextInt();
        String byX = scanner.next("x");
        int matrixHeight = scanner.nextByte();
        int[][] matrixInput = new int[matrixHeight][matrixWidth];
        for (int r = 0; r < matrixHeight; r++)
            for (int c = 0; c < matrixWidth; c++)
                matrixInput[r][c] = scanner.nextInt();

        return matrixInput;
    }

    public void printMatrix(int[][] matrix) {
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++)
                System.out.print(matrix[r][c] + " ");

            System.out.println();
        }
    }
}
