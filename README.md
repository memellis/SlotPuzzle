
![Build Release Workflow](https://github.com/memellis/SlotPuzzle/actions/workflows/build_release_workflow.yml/badge.svg)
![Build Release Workflow - push](https://github.com/memellis/SlotPuzzle/actions/workflows/build_release_workflow.yml/badge.svg?event=push)

SLOTPUZZLE
==========

SlotPuzzle is a puzzle game inspired by Fruit/Slot Machines. 

SlotPuzzle-2d Build Instructions
================================

Assumptions:
- You have cloned the SlotPuzzle GitHub hosted repository
- For example if you are me, ie to able to commit changes:
```
	git clone git://github.com/memellis/SlotPuzzle
```
  or if you are not me!
```
	git clone https://github.com/memellis/SlotPuzzle.git
```
  and to get the submodules:
```
	git submodule update --init
```
- To use Android Studio which is what I use:

	Click on File -> Open
	Navigate to source/java/2d and click OK

- In Andriod Studio to run SlotPuzzle desktop:
  1. In the project view in the left hand pane
  2. Navigate to desktop/src/com/ellzone/slotpuzzle2d/desktop
  3. Right click on DesktopLauncher
  4. Select Run DesktopLauncher.main
  5. The program will fail with a:
  -	 GdxRuntimeException caused by:

		"Couldn't load dependencies of asset: levels/mini slot machine level.tmx"
  6. Select Run -> Edit Configurations 
  7. In the Run Configurations dialog in the left hand pane
  8. Select Application DesktopLauncher
  9. In the right hand pane for Working Directory click on the folder icon
 10. Navigate to desktop/assets
 11. Click on Apply and OK
 12. Now Click on the Run Icon in the Android Studio tool bar

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
