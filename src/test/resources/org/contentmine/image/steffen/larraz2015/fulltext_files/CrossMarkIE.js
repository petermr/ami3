/*
	CrossMarkIE.js
			Crossmark 2.0 Widget does not display in IE versions below 9
			Display text explaining this
			Make sure function names and variable do not conflict with Prospect.js using cm_

*/
var cm_Browser = cm_getBrowser();
var cm_IEversion = cm_getIEVersion();
if (cm_Browser == "Internet Explorer")
{
	if (cm_IEversion)
	{
	   	var parent = cm_getObj("crossmark_container");
		if (null == parent)
		{
		}
		else
		{
			var child = cm_getObj("crossmark-content");
			var newnode = document.createElement("div");
			newnode.setAttribute("style", "color: silver;");
			newnode.style.cssText = ("color:silver") //For IE7 and below
			var textnode = document.createTextNode("To enable CrossMark in Internet Explorer please use version 9 or above");
			newnode.appendChild(textnode);
			parent.replaceChild(newnode,child);
		}
	}
}

function cm_getIEVersion()
{
		if (/MSIE (\d+\.\d+);/.test(navigator.userAgent))
		{ 
			var ieversion=new Number(RegExp.$1) // capture x.x portion and store as a number
			if (ieversion < 9 )
			{
			return true
			}
			else
			{
			return false
			}
		}
		else
		{
		return false
		}
}

function cm_getBrowser()
{
	var agt=navigator.userAgent.toLowerCase();
	if(agt.indexOf("msie") != -1) return 'Internet Explorer';
	else return navigator.userAgent;
}

function cm_getObj(name)
{
	  if (document.getElementById)
	  {
	  return document.getElementById(name);
	  }
	  else if (document.all)
	  {
	  return document.all[name];
	  }
	  else if (document.layers)
	  {
	  return document.layers[name];
	  }
}
	
	
	