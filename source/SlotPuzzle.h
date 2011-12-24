#ifndef _SLOTPUZZLE_H
#define _SLOTPUZZLE_H


class GameBoard;
class ScoreBoard;

class SlotPuzzle {
  public:
    void initialiseGame();

    void startGame();

    void endGame();


  private:
    ScoreBoard * sb;

};
#endif
