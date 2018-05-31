[![Build Status](https://travis-ci.org/memellis/SlotPuzzle.svg?branch=master)](https://travis-ci.org/memellis/SlotPuzzle)
<a href="https://codecov.io/github/rollup/rollup?branch=master">
    <img src="https://codecov.io/github/rollup/rollup/coverage.svg?branch=master" alt="Coverage via Codecov" />
</a>

SLOTPUZZLE
==========

SlotPuzzle is a puzzle game inspired by Fruit/Slot Machines. 

SlotPuzzle-2d Build Instructions
================================

Assumptions:
- You have cloned the SlotPuzzle GitHub hosted repository
- For example:
	git clone git://github.com/memellis/SlotPuzzle

Pre-requisites:
- Uses libGDX with Gradle and Eclipse, so use the instructions at:

https://github.com/libgdx/libgdx/wiki/Gradle-and-Eclipse

- skip the "You just generated your libgdx project, now it's time to start developing its guts in Eclipse!"
- because there is a generated project in the SlotPuzzle cloned GIT repository 

- but "Before you can import your project into Eclipse, make sure you setup your development environment!"

	https://github.com/libgdx/libgdx/wiki/Setting-up-your-Development-Environment-%28Eclipse%2C-Intellij-IDEA%2C-NetBeans%29

- to import the SlotPuzzle-2d gradle project from the cloned git repository directory:

	source/java/2d

- To see the state of the game running, in Eclipse, navigate to:

	SlotPuzzle2d-desktop/src/com.ellzone.slotpuzzle2d.desktop/DesktopLauncher.java
															
- Right click on DesktopLauncher.java and select Run As -> Java Application

- You should see the application running on your Desktop.

Using Gradle on the command line:

	https://github.com/libgdx/libgdx/wiki/Gradle-on-the-Commandline
