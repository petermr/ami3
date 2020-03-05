/**
*
* jQuery Advance Responsive Tabs Plugin
* URL: http://www.codecanyon.net/user/phpbits
* Version: 1.0
* Author: phpbits
* Author URL: http://www.codecanyon.net/user/phpbits
*
*/

// Utility
if ( typeof Object.create !== 'function' ) {
	Object.create = function( obj ) {
		function F() {};
		F.prototype = obj;
		return new F();
	};
}

(function( $, window, document, undefined ) {

	var construct_a_tab = {
		init: function( options, elem ) {
			var self = this;

			self.elem = elem;
			self.$elem = $( elem );

			self.options = $.extend( {}, $.fn.atabs.options, options );
			self.activeCss = {};
			self.activeCss['position'] = 'relative'; //set position from absolute
			self.activeCss['opacity'] = '1';
			self.activeCss['height'] = 'auto';

			self.inactiveCss = {};
			self.inactiveCss['position'] = 'absolute'; //set position from absolute

			//classes
			self.tab = 'a-tabs';
			self.nav = 'a-tab-nav';
			self.container = 'a-tab-container';
			self.contentItem = '.a-tab-container .a-tab-content';
			self.responsive = 'a-tabs-responsive';
			self.desktop = 'a-tabs-desktop';
			self.mobile = 'a-tabs-mobile';
			self.switchMobile = 'a-tabs-to-mobile';

			self.display();
			self.buildControls();
			self.buildResponsive(); 
		},
		display: function(){
			var self = this;

			self.$elem.addClass(self.tab); //add class to tab container
			self.$elem.find('.' + self.nav).addClass(self.responsive);
			self.$elem.find( self.contentItem + ':first' ).addClass('a-tab-active');
			self.$elem.find( '>ul > li:first-child' ).addClass('a-tab-active');
			self.$elem.find('>ul > li:first-child').addClass('a-tab-first');
			self.$elem.find('>ul > li:last-child').addClass('a-tab-last');
			self.$elem.find('.'+ self.container +' .a-tab-active').css(self.activeCss);
			self.$elem.prepend('<ul class="'+ self.nav +' '+ self.mobile +'"><li><a href=""><span class="a-mobile-title">'+ self.$elem.find('.a-tab-nav .a-tab-active a').html() +'</span><span class="a-tab-mobile-menu entypo list"></span></a></li></ul>');
		},
		buildControls: function(){
			var self = this;
			var active = '';
			var tablist = '.a-tab-nav li';
			self.$elem.find(tablist + ' > a').on('click',function(e){
				active = $(this).attr('href');
				activeEl =  self.contentItem + active;
				activeH = self.$elem.find(activeEl).css('height', 'auto').height();
				tabNav = $(this).parent('li').parent('ul');

				if(!tabNav.hasClass(self.mobile)){
					self.$elem.find(tablist).removeClass('a-tab-active');
					self.$elem.find(self.contentItem).removeClass('a-tab-active');
				
					self.$elem.find(self.contentItem).animate({'opacity':0, 'height' : activeH + 'px'}, self.options.animSpeed,function(){
						self.$elem.find(self.contentItem + active).addClass('a-tab-active');
						self.$elem.find(self.contentItem).css(self.inactiveCss);
						self.$elem.find(activeEl).css(self.activeCss);
					});
					$(this).parent('li').addClass('a-tab-active');
				}
				//set current tab for mobile menu
				self.$elem.find('.a-tab-nav.'+ self.mobile +' li .a-mobile-title').html( $(this).html() );

				if(tabNav.hasClass(self.mobile)){
					self.$elem.find( '.' + self.desktop ).show();
				}else if(tabNav.hasClass(self.desktop)){
					self.$elem.find( '.' + self.desktop ).hide();
				}

				e.preventDefault();
			});
		},
		buildResponsive: function(){
			var self = this;
			var contentW = self.$elem.width();
			var getID = self.$elem.attr('id');
			var width = 0;
			$('#'+ getID +'.a-tabs ul.'+ self.responsive +' li').each(function() {
			    var $this = $(this);
			    width += $this.outerWidth();
			});
			$( window ).resize(function() {
				contentW = self.$elem.width(); 
				self.changeView(contentW,width);
			});
			self.changeView(contentW,width);
		},
		changeView: function(contentW, width){
			var self = this;

			if( width >  contentW){
				self.$elem.addClass(self.switchMobile);
				self.$elem.find( '.' + self.responsive).addClass(self.desktop);
			}else{
				self.$elem.removeClass(self.switchMobile);
				self.$elem.find( '.' + self.responsive).removeClass(self.desktop);
				self.$elem.find( '.' + self.responsive).show();
			}

			if( contentW < 481 ){
				self.$elem.addClass('a-tab-content-480');
			}
		}
	};

	$.fn.atabs = function( options ) {
		return this.each(function() {
			var a_tab = Object.create( construct_a_tab );
			
			a_tab.init( options, this );

			$.data( this, 'atabs', a_tab );
		});
	};

	$.fn.atabs.options = {
		animSpeed : 200
	};

})( jQuery, window, document );