# Dynatrace-AppMon-UFO-Alerter
Alerter plugin for Dynatrace AppMon (>=6.5) to send GET request to UFO lamp.

__Description:__ This AppMon plugin can do a GET request to a UFO to change the status in case of a triggered incident.

__Name and version:__ UFO_Alerter version 1.0.0

__Compatible with:__ Dynatrace AppMon 6.5+

__Author:__ Sjoerd Bakker (IctCoreBiz)

__License:__ MIT

__Support Level:__ Unsupported, if you fix something, please create a pull-request :)

__Download latest version:__ [[released/biz.ictcore.dynatrace.ufo_1.0.0.jar]]

# Description
This plugin is created to allow AppMon to update the status of the UFO. If you use this plugin as action in an incident, you can create 2 actions, the "on incident begin" action to set a color and the "on incident end" action to reset back to normal.

e.g. 

"on incident begin": top=0|5|ff0000

"on incident end": top=0|5|00ff00

This will use the first 5 leds in color RED for begin and GREEN for end, without influencing anything else on the UFO.

