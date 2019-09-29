var selectionRect = {
	currentRect		: null,
	previousRect : null,
	currentY		: 0,
	currentX		: 0,
	originX			: 0,
	originY			: 0,
	
	setRectAndRememberPrevious: function(ele) {
		this.previousRect = this.currentRect;
		this.currentRect = ele;
	},

	getNewXYRecalculateWH: function() {
		var x = this.currentX<this.originX?this.currentX:this.originX;
		var y = this.currentY<this.originY?this.currentY:this.originY;
		var width = Math.abs(this.currentX - this.originX);
		var height = Math.abs(this.currentY - this.originY);
		return {
	        x       : x,
	        y       : y,
	        width  	: width,
	        height  : height
		};
	},
	getCurrentRectCoordsAsObject: function() {
		// use plus sign to convert string into number
		var x = +this.currentRect.attr("x");
		var y = +this.currentRect.attr("y");
		var width = +this.currentRect.attr("width");
		var height = +this.currentRect.attr("height");
		return {
			x1  : x,
	        y1	: y,
	        x2  : x + width,
	        y2  : y + height
		};
	},
	// thes will be listed at the top
	getCurrentRectCoordsAsText: function() {
		var rectCoords = this.getCurrentRectCoordsAsObject();
		return "x1: " + rectCoords.x1 + " x2: " + rectCoords.x2 + " y1: " + rectCoords.y1 + " y2: " + rectCoords.y2;
	},
	createNewRect: function(newX, newY) {
		// create a new rectangle
		var rectElement = svg.append("rect")
		    .attr({
		        rx      : 4,
		        ry      : 4,
		        x       : 0,
		        y       : 0,
		        width   : 0,
		        height  : 0
		    })
		    .classed("selection", true);
	    this.setRectAndRememberPrevious(rectElement);
		this.originX = newX;
		this.originY = newY;
		this.updateXYandWH(newX, newY);
	},
	updateXYandWH: function(newX, newY) {
		this.currentX = newX;
		this.currentY = newY;
		this.currentRect.attr(this.getNewXYRecalculateWH());
	},
	updateStrokeAndWidth: function() {
		messageText.text("updateStrokeAndWidth");
        this.currentRect
            .style("stroke", annotator.color)
			.style("stroke-width", "2")
			.style("fill", "none");
			this.currentRect.id = annotator.type;
			this.currentRect.title = annotator.type;
			this.currentRect.append("title").text(annotator.type);
		
    },
    removeCurrentRect: function() {
    	this.currentRect.remove();
    	this.currentRect = null;
    },
    removePreviousRect: function() {
		// this allows previous rect to be removed
		if (true) return
    	 if(this.previousRect) {
    	 	this.previousRect.remove();
    	 }
    }
	
};

// created by D3 from SVG
var svg = d3.select("svg");
// created from (p) elements with attributes
var clickTime = d3.select("#clicktime");
var attributesText = d3.select("#attributestext");
var messageText = d3.select("#messagetext");


function dragStart() {
	message("dragStart");
    var xy = d3.mouse(this);
	messageText.text(""+xy);
    selectionRect.createNewRect(xy[0], xy[1]);
	selectionRect.removePreviousRect();
}

function dragMove() {
	message("dragMove");
	var xy = d3.mouse(this);
    selectionRect.updateXYandWH(xy[0], xy[1]);
    attributesText
    	.text(selectionRect.getCurrentRectCoordsAsText());
}

function dragEnd() {
	message("dragEnd");
	var finalCoords = selectionRect.getCurrentRectCoordsAsObject();
	console.dir(finalCoords);
	if(finalCoords.x2 - finalCoords.x1 > 1 && finalCoords.y2 - finalCoords.y1 > 1){
		messageText.text("dragEnd");
		console.log("range selected");
		// range selected
		d3.event.sourceEvent.preventDefault();
		selectionRect.updateStrokeAndWidth();
	} else {
		messageText.text("single point no drag");		
		console.log("single point");
        // single point selected
        selectionRect.removeCurrentRect();
        // trigger click event manually
        clicked();
    }
}

var dragBehavior = d3.behavior.drag()
    .on("drag", dragMove)
    .on("dragstart", dragStart)
    .on("dragend", dragEnd);



svg.call(dragBehavior);

var doiPicker = document.querySelector('input[name="doiButton"]');
var titlePicker = document.querySelector('input[name="titleButton"]');
var authPicker = document.querySelector('input[name="authButton"]');
var annotator = {
		color : "red",
		type : "doi"
}


function textClick() {
	message("testClick");
}

function handleMouseOver(d, i) {
	message("handleMouseOver");
}

function handleMouseOut(d, i) {
	message("mouseOutX");
}

function clicked() {
	var d = new Date();
    clickTime
    	.text("Clicked at " + d.toTimeString().substr(0,8) + ":" + d.getMilliseconds());
}
function setColorTypeReport(color, type) {
	message(type);
	annotator.color = color;
	annotator.type = type;
}

function message(message) {
	messageText.text(message);	console.log(message);
}
function selectDOI()         	{setColorTypeReport("red",     "doi");}
function selectTitle()       	{setColorTypeReport("green",   "title");}
function selectAuth()        	{setColorTypeReport("blue",    "auth");}
function selectAffil()       	{setColorTypeReport("cyan",    "affil");}
function selectEmail()       	{setColorTypeReport("orange",  "email");}
function selectAbstract()    	{setColorTypeReport("magenta", "abstract");}
function selectColumn()      	{setColorTypeReport("#ffaacc", "column");}
function selectSectionHead() 	{setColorTypeReport("cyan",    "section");}
function selectSubSectionHead() {setColorTypeReport("#77ffaa", "subSection");}
function selectBiblio()      	{setColorTypeReport("#ccff77", "biblio");}
function selectPage()        	{setColorTypeReport("#cc77ff", "page");}
function selectFigure()      	{setColorTypeReport("#ffcc77", "figure");}
function selectFigureCaption()  {setColorTypeReport("#77ccff", "figureCaption");}
function selectTable()       	{setColorTypeReport("#77ffcc", "table");}
function selectTableTitle()  	{setColorTypeReport("#00ccff", "tableTitle");}
function selectTableHead()   	{setColorTypeReport("#00ffcc", "tableHead");}
function selectTableBody()   	{setColorTypeReport("#ff00cc", "tableBody");}
function selectTableFoot() 	    {setColorTypeReport("#ffcc00", "tableFoot");}
function selectMaths()       	{setColorTypeReport("#ccff00", "maths");}
function selectReferences()  	{setColorTypeReport("#cc77aa", "references");}

