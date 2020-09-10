 (function ($) {
        function grab_content(span_id) {
            var content = $('*[id="' + span_id + '"]').text();

            if (span_id.indexOf("fn", 0) != -1) {
                return content; // could contain useful anchor text i.e. see doi:
            }
            else {
                // strip external anchor text CrossRef etc. (but not Dera linked text)
               $('span[id="' + span_id + '"] a[class !="simple"][class !="CH"][class !="TC"][class !="RTC"][class !="GB"]').each(
            		function () {
            					content = content.replace($(this).text(), '');
              					}); return content;
            }
       }

        $.fn.swapAttr = function (attribute) {
            return this.each(function () {
                $(this).hover(function () {
                    var $this = $(this);
                    var idval = $this.attr("href").replace("#", "").replace("img","");
                    var textval = grab_content(idval);
                    if (textval.replace(/\s+/g, '') != "") {
                        $this.attr('title', textval.replace(/\s+/g, ' '));
                    }
                }, function () {
                    var $this = $(this);
                });
            });
        };

    })(jQuery);

    $(document).ready(function () {
        $("a[href^='#']").swapAttr();
    });