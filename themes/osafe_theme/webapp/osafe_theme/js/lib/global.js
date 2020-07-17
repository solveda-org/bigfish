/*--------------------------------------------------------------------------*/

// Prototype-based javascript window dimensions
// http://textsnippets.com/posts/show/835
Position.GetWindowSize = function(w) {
	w = w ? w : window;
	var width = w.innerWidth || (w.document.documentElement.clientWidth || w.document.body.clientWidth);
	var height = w.innerHeight || (w.document.documentElement.clientHeight || w.document.body.clientHeight);
	return [width, height];
};

/*--------------------------------------------------------------------------*/

// Center a DOM element, prototype based
// http://textsnippets.com/posts/show/836
Position.Center = function(element, parent) {
	var w, h, pw, ph;
	w = element.offsetWidth;
	h = element.offsetHeight;
	Position.prepare();
	if (!parent) {
		var ws = Position.GetWindowSize();
		pw = ws[0];
		ph = ws[1];
	} else {
		pw = parent.offsetWidth;
		ph = parent.offsetHeight;
	}
	element.style.top = Math.round((ph/2) - (h/2) + Position.deltaY) + 'px';
	element.style.left = Math.round((pw/2) - (w/2) - Position.deltaX) + "px";

//	element.style.top = Math.round(Position.deltaY + 175) + 'px';
//	element.style.left = Math.round((pw/2) - (w/2) -  Position.deltaX) + "px";
};

/*--------------------------------------------------------------------------*/
var PopUp = Class.create();
PopUp.i = 0;
PopUp.prototype = {
	initialize: function(trigger, popup) {
		PopUp.open   = false;
		this.trigger = trigger;
		this.popup   = $(popup);
		this.options = Object.extend({}, arguments[3] || {});
		this.start();
	},
	start: function() {
		if (this.trigger instanceof Array) {
			this.triggers = this.trigger;
		} else {
			this.triggers = [this.trigger];
		}

		if(this.popup){
		this.popup.hide();
		}
		this.triggers.each(function(t) {
			Event.observe(t, 'click', this.openInternal.bindAsEventListener(this));
		}.bind(this));
	},
	openInternal: function(event) {
		this.toTop();
		Position.Center(this.popup, $('container'));

		this.closebtn  = this.popup.getElementsBySelector('.close').first();
		this.handle    = this.popup.getElementsBySelector('.tbar').first();
		this.draggable = new Draggable(this.popup, {handle: this.handle, starteffect: false, endeffect: false });

		this.closeListener = this.closeInternal.bindAsEventListener(this);
		Event.observe(document, 'click', this.closeListener);
		Event.observe(this.closebtn, 'click', this.closeListener);
		Event.observe(this.popup, 'click', function(event) {
			this.falseAlarm = true;
			this.toTop();
		}.bind(this));
		Event.stop(event);

		if (PopUp.open) { PopUp.open.closeInternal(false); }
		PopUp.open = this;

		(this.options.onOpen || Prototype.emptyFunction)();
	},
	closeInternal: function(event) {
		if (this.falseAlarm) {
			this.falseAlarm = false;
			return;
		}

		PopUp.open = false;
		this.popup.hide();

		this.draggable.destroy();

		Event.stopObserving(document, 'click', this.closeListener);
		Event.stopObserving(this.closebtn, 'click', this.closeListener);
		Event.stop(event);

		(this.options.onClose || Prototype.emptyFunction)();
	},
	toTop: function() {
		PopUp.i = PopUp.i + 1;
		this.popup.style.zIndex = PopUp.i + 120;
		this.popup.show();
	}
};

/*--------------------------------------------------------------------------*/
/*--------------------------------------------------------------------------*/

var Highlights = Class.create();
Highlights.prototype = {
	initialize: function(container) {
		this.container = $(container);
		this.container && this.setup();
	},
	setup: function() {
		var items = $$('.item');
		items.each(function(el) {
			Event.observe(el, 'mouseover', function() {
				Element.addClassName(el, 'active');
			});

			Event.observe(el, 'mouseout', function() {
				Element.removeClassName(el, 'active');
			});

			Event.observe(el, 'click', function() {
				var link = el.getElementsByTagName('a')[0];
				document.location.href = link.href;
			});
		});
	}
};


// Scroll to an element inside an overflow:auto container.
// http://elia.wordpress.com/2007/01/18/overflow-smooth-scroll-with-scriptaculous/
Effect.MoveTo = Class.create();
Object.extend(Object.extend(Effect.MoveTo.prototype, Effect.Base.prototype), {
	initialize: function(element) {
		this.element = $(element);
		var options = Object.extend({
			x: 0,
			y: 0,
			to_element: null,
			mode: 'absolute'
		} , arguments[1] || {} );
		this.start(options);
	},
	setup: function() {
		if (this.options.continuous && !this.element._ext ) {
			this.element.cleanWhitespace();
			this.element._ext=true;
			this.element.appendChild(this.element.firstChild);
		}
		this.originalLeft=this.element.scrollLeft;
		this.originalTop=this.element.scrollTop;
		if (this.options.to_element) {
			toElement=$(this.options.to_element);
			container_dims=this.element.getDimensions();
			to_element_dims=toElement.getDimensions();
			Position.prepare();
			containerOffset = Position.cumulativeOffset(this.element);
			elementOffset = Position.cumulativeOffset(toElement);
			this.options.x=this.options.x+elementOffset[0]-containerOffset[0]-(container_dims.width/2 - to_element_dims.width/2);
			this.options.y=this.options.y+elementOffset[1]-containerOffset[1]-(container_dims.height/2 - to_element_dims.height/2);
		}
		if(this.options.mode == 'absolute') {
			this.options.x -= this.originalLeft;
			this.options.y -= this.originalTop;
		}
	},
	update: function(position) {
		this.element.scrollLeft = this.options.x * position + this.originalLeft;
		this.element.scrollTop = this.options.y * position + this.originalTop;
	}
});

/*--------------------------------------------------------------------------*/

Effect.Persistent = Class.create();
Effect.Persistent.prototype = {
	initialize: function(element, container) {
		this.element   = $(element);
		this.container = $(container);
		this.setup();
	},
	setup: function() {
		Event.observe(window, 'scroll', this.test.bind(this));
	},
	test: function() {
		if (this.timeout) {
			clearTimeout(this.timeout);
		} else {
			Position.prepare();
			this.startY = Position.deltaY;
		}
		this.timeout = setInterval(this.slide.bind(this), 300);
	},
	slide: function() {
		Position.prepare();
		if (this.startY == Position.deltaY) {
			clearInterval(this.timeout);
			this.timeout = null;
			this.move(Position.deltaY);
		}
		this.startY = Position.deltaY;
	},
	move: function(y) {
		Position.prepare();
		var pos       = Position.cumulativeOffset(this.element.up());
		var height    = this.element.getHeight();
		var maxHeight = this.container.getHeight();

		if (y + height > maxHeight) {
			new Effect.Move(this.element, {x:0, y: maxHeight - height - 30, mode: 'absolute'});
		} else if (y > (pos[1] - 10)) {
			new Effect.Move(this.element, {x:0, y: y - (pos[1] - 10), mode: 'absolute'});
		} else {
			new Effect.Move(this.element, {x:0, y: 0, mode: 'absolute'});
		}
	}
};

Event.onDOMReady(function(){
});
