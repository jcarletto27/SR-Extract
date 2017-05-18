# SR-Extract
This is a simple JavaFX project to Load Rayware's proprietary file format SSJ as well as Zips containing PNG files for use with Resin Printers.

Note: This app can only load SSJ files that have the "Enhanced" box ticked in Rayware.


Features:

	Accepts drag and dropped files of both Zip and SSJ.
	
	Displays a quick slide show to validate imported images. (Default ON)
	
	Generates an Info.json file for use with NanoDLP software, currently only supports [LargestArea] function. (Default ON)
	
	Shows a scaled representation of the image as it should appear. (Default ON)
	
	Picks correct micron resolution based on SSJ file, supports 75 and 100 micron. (Default 100)
	
	Supports Anti-aliasing output PNG files (Must be changed in the config.properties file, Default 0, this adds more time to the export when increased)
	
	Stores last opened and saved locations.
	
	
