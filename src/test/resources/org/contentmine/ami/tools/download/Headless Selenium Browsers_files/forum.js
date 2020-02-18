/*globals $,ReplyBoxHandler,OverlayHandler,EditCommentBoxHandler*/
/* takes care of the hovers for the tree comments, leave a comment link, Reply links in the flat comments, tree
 * and flat comments contents */
var CommentsHandler = {

    referenceCommentId : -1, // the id of the comment that is about to become a parent of the new comment
    subjectTouched: false,
    bodyTouched: false,

    activate : function () {
        'use strict';
        // take care of the relative times in the flat comments as well as the tree comments
        this.populateAgoTimes();
        this.activateReplyLinks();
        this.activateEditLinks();
        this.activateRootPost();
    },

    showNotificationInput : function () {
        if (CommentsHandler.bodyTouched
                && CommentsHandler.subjectTouched
                && $.trim($('#subject').val()).length !== 0
                && $.trim($('#body').val()).length !== 0) {
            $('#comment_here input').attr('checked', forumNotificationDefault);
            $('#comment_here input[name=emailMe], #comment_here span.tocheck').fadeIn(3000);
        }
    },

    populateAgoTimes : function () {
        'use strict';
        var that = this;
        $('li[id^=anch]').each(function () {
            that.populateAgoTime(this);
        });
    },

    populateAgoTime : function (flatCommentNode) {
        'use strict';
        var commentId,
            agoTime = this.getRelativeTime($('input[name=tmstmp]', flatCommentNode).attr('value'));
        $('span.bodyRelativeTime', flatCommentNode).text(agoTime);
        commentId = $(flatCommentNode).attr('id').substring(4);
        commentId = parseInt(commentId);
        $('#com' + commentId + ' span.treeRelativeTime').text(agoTime);
    },

    activateReplyLinks : function () {
        'use strict';
        var that = this;
        $('.reply_flat').click(function (event) {
            if(!loggedIn) {
                event.preventDefault();
                infoq.event.trigger('login', { ref: 'commentsLogin' });
            } else {
                PageNotifier.hideNotificationPopup();
                var $referenceCommentElement = $(this).closest('.comment');
                that.referenceCommentId = $referenceCommentElement.attr('id').substring(4);
                that.referenceCommentBody = "";
                if($referenceCommentElement.length > 0) {
                    that.referenceCommentBody = $referenceCommentElement.children(".flat_comment_body").html();
                }
                that.activateReplyLink($referenceCommentElement);
            }
        });
    },

    activateReplyLink : function (anchor) {
        'use strict';
        $("#replyPopup").detach().appendTo(anchor);
        ReplyBoxHandler.replyBoxAnchor = anchor;
        ReplyBoxHandler.showReplyBox();
    },

    activateRootPost : function () {
        if(loggedIn){
            // for logged in we need to remove this element! (this is the hello stranger div).
            $('#postFormDeck').remove();
        };

        $('#comment_here')
                .hover(function () {
                    if (!loggedIn) {
                        $('#postFormDeck').css('display', 'block');
                    }
                },
                function () {
                    $('#postFormDeck').hide();
                });
        $('#subject').val(JSi18n.enter_subject);
        $('#body').val(JSi18n.enter_message);
        $('#submitComment').click(function (event) {
            if (!loggedIn) {
                event.preventDefault();
                infoq.event.trigger('login', { ref: 'rootCommentLogin' });
            } else {
                CommentsPoster.postRootComment();
            }
        });

        $('#subject,#body').keyup(CommentsHandler.showNotificationInput);
        $('#subject').
                focusin(function() {
                    if (!loggedIn) {
                        $('#postFormDeck').css('display', 'block');
                        return;
                    }
                    if (CommentsHandler.subjectTouched) {
                        return;
                    }
                    CommentsHandler.subjectTouched = true;
                    $(this).val('');
        });
        $('#body')
                .focusin(function() {
                    if (!loggedIn) {
                        $('#postFormDeck').css('display', 'block');
                        return;
                    }
                    if (CommentsHandler.bodyTouched) {
                        return;
                    } 
                    CommentsHandler.bodyTouched = true;
                    $(this).val('');
                    $('#body').autosize(); // call this after the font is updated, it only reads font related style properties once
                });
    },

    activateEditLinks : function () {
        'use strict';
        $('li[id^=anch]').each(function () {
            CommentsHandler.activateEditLink(this);
        });
    },

    activateEditLink : function (flatComment) {
        'use strict';
        var that = this,
            editLink = $('.edit_comment', flatComment).first();
        // do nothing if the edit link is not available
        if (!editLink) {
            return;
        }
        that.updateCountdownLink(editLink);
        if (editLink) { //might have been deleted as part of the previous method call
            $(editLink).click(function (e) {
                e.preventDefault();
                // the tree comment will need the reference id
                that.referenceCommentId = $(this).closest('.comment').first().attr('id').substring(4);
                EditCommentBoxHandler.clearEditCommentBox();
                EditCommentBoxHandler.prepopulateSubjectAndBody();
                EditCommentBoxHandler.showEditCommentBox($(this).closest('.comment').first());
            });
        }
    },

    updateCountdownLink : function (linkNode) {
        'use strict';
        var parent = $(linkNode).closest('.comment').first(),
            msDelta = new Date().getTime() - parseInt($('input[name=tmstmp]', parent).val()),
            minutesRemaining = commentEditPeriod - Math.floor(msDelta / (1000 * 60));
        if (minutesRemaining <= 0) {
            // remove the edit link from the node
            $(linkNode).hide();
        } else {
            if (minutesRemaining === 1) {
                minutesRemaining = '< 1';
            }
            $(linkNode).text(JSi18n.editLinkFormat.replace(/\$m/, minutesRemaining));
            setTimeout(function () {
                CommentsHandler.updateCountdownLink(linkNode);
            }, 10 * 1000); // 10 seconds error allowed
        }
    },

    getRelativeTime : function (timeInMilliseconds) {
        'use strict';
        // create a new date with the browser locale
        var date = new Date();
        // set the milliseconds to this localized date
        date.setTime(timeInMilliseconds);
        // get the relative time
        moment.lang(InfoQConstants.language);
        var momentDate = moment(date);
        var momentNow = moment();
       
        if(momentNow.diff(momentDate,"days")>=3){
	      	// moment.js needs upper case for date format to work as we have for content.
	      	return momentDate.format(JSi18n.content_datetime_format.toUpperCase()+" hh:mm");
        }else{
        	return momentDate.fromNow();
        }        
    },

    clearRootPostBox : function () {
        $('#subject').val('');
        $('#body').val('');
        $('#comment_here input[name=emailMe], #comment_here span.tocheck').hide();
        CommentsHandler.bodyTouched = false;
        CommentsHandler.subjectTouched = false;
    },
    
    quoteOriginalMessage : function() {
    	if(this.referenceCommentBody && this.referenceCommentBody != "") {
    		$('textarea.commentsReply').val($('textarea.commentsReply').val() + "<blockquote>" + $.trim(this.referenceCommentBody) + "</blockquote>");
    	}
    	
    }
};

$(function () {
    'use strict';
    //CommentsHandler.activate();
    var commentsButton = $('#noOfComments');
    commentsButton.text($('#commentLinkText').text());
    $('#noOfComments').show();
});/*globals $,ReplyBoxHandler,OverlayHandler,CommentsHandler,PageNotifier*/

/* takes care of the comments posting functionality: form validation, result processing*/
var CommentsPoster = {
    postComment : function () {
        'use strict';
        // protect against multiple submits        
        CommentsPoster.disableForumPost("submit-reply", "replyPopup"); 
        
        var replyBox = $('#replyPopup'),
            subject = $('.subject', replyBox).val(), // new comment subject
            body = Parser.fixTags($('textarea.commentsReply', replyBox).val()), // new comment body
            notification = $('.emailMe', replyBox).is(':checked'); // email me checkbox
            //keep the setting for this session in case the user unchecks the default
            forumNotificationDefault = notification;

        // make sure valid data is submitted, do nothing otherwise
        if ($.trim(subject).length === 0) {
            PageNotifier.showNotificationPopup(JSi18n.errorSubject, CommentsPoster.redrawReplyBox, $("#replyPopup"));
            this.enableForumPost("submit-reply", "replyPopup");
            return;
        }
        if ($.trim(body).length === 0) {
			PageNotifier.showNotificationPopup(JSi18n.errorBody, CommentsPoster.redrawReplyBox, $("#replyPopup"));
			CommentsPoster.enableForumPost("submit-reply", "replyPopup");
			return;
        }
        // NOTE: the callback with params is possible becaus ethe function beeing called here returns another function that does the desired effect
        UserActions_Profile.forceUpdateProfile(CommentsPoster.sendRequest(postAddress, subject, body, notification, CommentsHandler.referenceCommentId, CommentsPoster.handleResponse));        
    },

    postRootComment : function () {
    	// protect against multiple submits
    	CommentsPoster.disableForumPost("submitComment", "comment_here");
    	
        var notification = $('#emailMe').is(':checked'),
            subject = $('#subject').val(),
            body = $('#body').val();

        // validation
        if (!CommentsHandler.subjectTouched || $.trim(subject).length === 0) {
            PageNotifier.showNotificationPopup(JSi18n.errorSubject);
            CommentsPoster.enableForumPost("submitComment", "comment_here");
            return;
        }
        if (!CommentsHandler.bodyTouched || $.trim(body).length === 0) {
			PageNotifier.showNotificationPopup(JSi18n.errorBody);
			CommentsPoster.enableForumPost("submitComment", "comment_here");
			return;
        }
        // NOTE: the callback with params is possible becaus ethe function beeing called here returns another function that does the desired effect
        UserActions_Profile.forceUpdateProfile(CommentsPoster.sendRequest(postAddress, subject, body, notification, -1, CommentsPoster.handleResponse));        
    },

    repostComment : function () {
        'use strict';
        // protect against multiple submits
        CommentsPoster.disableForumPost("resubmit-reply", "editCommentPopup");
        
        var editCommentBox = $('#editCommentPopup'),
            subject = $('.subject', editCommentBox).val(), // comment subject
            body = Parser.fixTags($('.commentsReply', editCommentBox).val()), // comment body
            notification = $('.emailMe', editCommentBox).is(':checked'); // email me checkbox

        // make sure valid data is submitted, do nothing otherwise
        if ($.trim(subject).length === 0) {
            PageNotifier.showNotificationPopup(JSi18n.errorSubject, CommentsPoster.redrawEditBox, $("#editCommentPopup"));
            CommentsPoster.enableForumPost("resubmit-reply", "editCommentPopup");
            return;
        }
        if ($.trim(body).length === 0) {
			PageNotifier.showNotificationPopup(JSi18n.errorBody, CommentsPoster.redrawEditBox, $("#editCommentPopup"));
			CommentsPoster.enableForumPost("resubmit-reply", "editCommentPopup");
			return;
        }
        // NOTE: the callback with params is possible becaus ethe function beeing called here returns another function that does the desired effect
        UserActions_Profile.forceUpdateProfile(CommentsPoster.sendRequest(repostAddress, subject, body, notification, CommentsHandler.referenceCommentId, CommentsPoster.handleRepostResponse));
    },

    sendRequest : function (address, subject, body, notification, parentMessageId, responseCallback) {
        var sendAjaxRequest = function(){
	    	$.ajax({
                        url: address,
                        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                        type: 'POST',
	            data: {
	                'reply' : 'true',
	                'forumID' : forumID,
	                'threadID' : threadID,
	                'messageID' : parentMessageId,
	                'subject' : subject,
	                'notification' : notification, // email me checkbox
	                'contentUrl' : document.location.href,
                    'contentTitle': contentTitle,
                    'authorUserCSVIds': authorUserCSVIds,
	                'body' : body
	            },
	            success: responseCallback,
				error: function (jqXHR, textStatus, exception) {
                    var reportLink = ". <a href='mailto:feedback@infoq.com?subject=Error posting comment&body=Posting the following comment on " + top.location.href + " does not work.%0D%0ASubject:%0D%0A" + subject + "%0D%0ABody:%0D%0A" + body + "'>Report this issue</a>.";
	                PageNotifier.showNotificationPopup(JSi18n.error + ": " + CommentsPoster.extractErrorMessage(jqXHR.responseText) + reportLink);
	            }
			});
            infoq.event.trigger("modalClose");
        }
        return sendAjaxRequest;
    },

    redrawReplyBox : function () {
        'use strict';
        ReplyBoxHandler.showReplyBox();
    },

    redrawEditBox : function () {
        EditCommentBoxHandler.showEditCommentBox(ReplyBoxHandler.replyBoxAnchor);
    },

    handleResponse : function (response) {
        'use strict';
        CommentsPoster.enableForumPost("submit-reply", "replyPopup");
        CommentsPoster.enableForumPost("submitComment", "comment_here");

        if ($.trim(response).substring(0, 6) === 'error:') {
            if (response.indexOf('Message contains invalid links') !== -1) {
                ReplyBoxHandler.showErrorMessage(JSi18n.errorInvalidLinks);
            } else if (response.indexOf('only guest user available, no logged in business user') !== -1) {
                loggedIn = false;
                PageNotifier.showNotificationPopup('[i18lize]Your session expired. Please log in and resubmit', ReplyBoxHandler.inviteToSignIn, $("#replyPopup"));
            } else {
                var reportLink = " <a href='mailto:feedback@infoq.com?subject=Error posting comment&body=Posting the following comment on " + top.location.href + " does not work.%0D%0ASubject:%0D%0A" + subject + "%0D%0ABody:%0D%0A" + body + "'>Report this issue</a>.";
                PageNotifier.showNotificationPopup($.trim(response).substring(6, response.length) + reportLink, undefined, $("#replyPopup"));
            }
        } else {
            var $responseHtml = $('<div/>').html(response).contents();
            var $newComment = $responseHtml.find("li.comment");
            var $newTreeComment = $('[id^=com]', $responseHtml);

            $('.comments__boxes').append($newComment);

            $('.reply_flat', $newComment).click(function () {
                var $commentDomElement = $(this).closest('.comment');
                CommentsHandler.referenceCommentId = $commentDomElement.attr("id").substring(4);
                CommentsHandler.referenceCommentBody = "";
                var $commentBody = $commentDomElement.children(".flat_comment_body");
                if($commentBody.length) {
                    CommentsHandler.referenceCommentBody = $commentBody.html();
                }
                CommentsHandler.activateReplyLink($commentDomElement);
            });

            // add it to the tree
            var parentComment = $('#com' + CommentsHandler.referenceCommentId).first();
            if (parentComment.length) {
                var parentCommentReplies = parentComment.find("ul");
                if(!parentCommentReplies.length) {
                    parentCommentReplies = $(document.createElement('ul'));
                    parentCommentReplies.addClass("comments__list");
                }
                parentCommentReplies.append($newTreeComment);
                parentComment.append(parentCommentReplies);
            } else {
                $(".comments__list").first().append($newTreeComment);
            }

            CommentsHandler.populateAgoTime($newComment);
            CommentsHandler.activateEditLink($newComment);

            ReplyBoxHandler.hideReplyBox();
            ReplyBoxHandler.clearReplyBox();
            CommentsHandler.clearRootPostBox();

            // scroll to the new flat comment
            $(window).scrollTop($($newComment).offset().top);
        }
    },

    handleRepostResponse : function (response) {
        'use strict';
        CommentsPoster.enableForumPost("resubmit-reply", "editCommentPopup");
        var oldContentFlat = $('#anch' + CommentsHandler.referenceCommentId);

        if ($.trim(response).substring(0, 6) === 'error:') {
            if (response.indexOf('Message contains invalid links') !== -1) {
                EditCommentBoxHandler.showErrorMessage(JSi18n.errorInvalidLinks);
            } else if (response.indexOf('You are not allowed to edit this message.') !== -1) {
                PageNotifier.showNotificationPopup(JSi18n.timeExpiredMessage, undefined, $("#editCommentPopup"));
            }  else if (response.indexOf('only guest user available, no logged in business user') !== -1) {
                loggedIn = false;
                PageNotifier.showNotificationPopup(JSi18n.sessionExpiredMessage, ReplyBoxHandler.inviteToSignIn, $("#editCommentPopup"));
            } else {
                PageNotifier.showNotificationPopup(response.substring(6, response.length), undefined, $("#editCommentPopup"));
            }
        } else {
            // update existing flat comment
            EditCommentBoxHandler.hideEditCommentBox();

            var $responseHtml = $('<div/>').html(response).contents();
            var $newComment = $responseHtml.find("li.comment");
            $('#anch' + CommentsHandler.referenceCommentId).html($($newComment).html());
            // update existing tree comment
            var editedTreeComment = $('[id^=com]', $responseHtml);

            $('#com' + CommentsHandler.referenceCommentId).html($(editedTreeComment).html());

            CommentsHandler.populateAgoTime(oldContentFlat);
            CommentsHandler.activateEditLink(oldContentFlat);

            // scroll to the new flat comment
            $(window).scrollTop($(oldContentFlat).offset().top);
        }
    },

    findNodeLevel : function (node) {
        'use strict';
        var classRaw = $(node).attr('class');
        classRaw = classRaw ? classRaw : '';
        var classArray = classRaw.split(/\s+/),
            level,
            i;
        for (i = 0; i < classArray.length; i++) {
            if (classArray[i].substring(0, 3) === 'lvl') {
                level = classArray[i].substring(3);
                break;
            }
        }
        return level;
    },
    
    disableForumPost:function(idButton, idPopup){    	
    	// forum post needs to be disabled to protect against multiple submit situations
    	$('#'+idButton).prop('disabled', true);
        $('#'+idPopup).css("cursor", "progress");
    },
    
    enableForumPost:function(idButton, idPopup){    	
    	// forum post was disabled to prevent multiple submits situations
    	$('#'+idButton).prop('disabled', false);
        $('#'+idPopup).css("cursor", "default");
    },
    
    extractErrorMessage:function(msg){
        var startIdx=msg.indexOf("[]")+2;
        var endIdx=msg.indexOf("[/]");
        return msg.substring(startIdx, endIdx);
    }
};/*globals $,ReplyBoxHandler,CommentsHandler*/

/*takes care of the box used to reedit the contens of previously edited comments */
var EditCommentBoxHandler = {
    activate : function () {
        'use strict';
        var that = this,
            editCommentBox = $('#editCommentPopup');
        $('.close_popup', editCommentBox).click(that.hideEditCommentBox);
        $('.reset-reply', editCommentBox).click(that.hideEditCommentBox);
        // activate repost button
        $('#resubmit-reply').click(CommentsPoster.repostComment);
    },

    clearEditCommentBox : function () {
        'use strict';
        var editCommentBox = $('#editCommentPopup');
        // clear subject and body
        $('input[name=subject]', editCommentBox).val('');
        $('.commentsReply', editCommentBox).val('');
        // hide invalid link error
        $('p.allowed.error', editCommentBox).css('display', 'none');
        // hide time expired error
        $('p.allowed.time_expired', editCommentBox).css('display', 'none');
        // enable the button
        $('#resubmit-reply', editCommentBox).removeAttr('disabled').removeClass('disabled');
    },

    showEditCommentBox : function (anchor) {
        'use strict';
        var that = this,
            editCommentBox = $('#editCommentPopup');

        ReplyBoxHandler.hideReplyBox();
        // do nothing if the edit link is clicked again
        if (anchor === ReplyBoxHandler.replyBoxAnchor) {
            return;
        }
        editCommentBox.detach().appendTo(anchor);
        editCommentBox.show();
        ReplyBoxHandler.replyBoxAnchor = anchor;
        that.updateCountdownButton();
    },

    updateCountdownButton : function () {
        'use strict';
        var myCommentId = CommentsHandler.referenceCommentId,
            creationTime = parseInt($('#anch' + CommentsHandler.referenceCommentId + ' input[name=tmstmp]').val()),
            button = $('#editCommentPopup #resubmit-reply');

        (function updateButtonText() {
            var msDelta = new Date().getTime() - creationTime,
                secondsRemaining = 60 * commentEditPeriod - Math.floor(msDelta / 1000);
            // if the reference comment id changed (another edit link was clicked, abort update
            if (myCommentId !== CommentsHandler.referenceCommentId) {
                return;
            }
            if (secondsRemaining < 0) {
                $(button).addClass('disabled').attr('disabled', 'disabled').val(JSi18n.timeExpiredButton);
                EditCommentBoxHandler.showTimeExpiredMessage(JSi18n.timeExpiredMessage);
            } else if (secondsRemaining <= 60) {
                $(button).val(JSi18n.repostButtonFormat.replace(/\$m/, secondsRemaining + 's'));
                setTimeout(updateButtonText, 1000);
            } else {
                $(button).val(JSi18n.repostButtonFormat.replace(/\$m/, Math.ceil(secondsRemaining / 60) + 'm'));
                if (secondsRemaining < 70) {
                    setTimeout(updateButtonText, 1000);
                } else {
                    setTimeout(updateButtonText, 10 * 1000);
                }
            }
        }());
    },

    hideEditCommentBox : function () {
        'use strict';
        var editCommentBox = $('#editCommentPopup');
        editCommentBox.detach().appendTo(".comments__boxes");
        editCommentBox.hide();
    },

    prepopulateSubjectAndBody : function () {
        'use strict';
        var editCommentBox = $('#editCommentPopup'),
            corrFlatComment = $('#anch' + CommentsHandler.referenceCommentId),
            isNotificationEnabled = $('input[name=isNotificationOn]', corrFlatComment).val() === 'true';
        $('.subject', editCommentBox).val($('.comment__title', corrFlatComment).text());
        $('.commentsReply', editCommentBox).val($.trim($('.flat_comment_body p', corrFlatComment).html()).replace(/<br>/g, '\n'));

        $('input[name=emailMe]', editCommentBox).attr('checked', isNotificationEnabled);
    },

    showErrorMessage : function (errorMessage) {
        'use strict';
        $('p.error.allowed', $('#editCommentPopup')).css('display', 'inline').text(errorMessage);
        setTimeout(EditCommentBoxHandler.hideErrorMessage, 3000);
    },

    hideErrorMessage : function () {
        'use strict';
        $('#editCommentPopup p.error.allowed').css('display', 'none').text('');
    },

    showTimeExpiredMessage : function (errorMessage) {
        'use strict';
        $('#editCommentPopup p.time_expired.allowed').css('display', 'block').text(errorMessage);
    },

    hideTimeExpiredMessage : function () {
        'use strict';
        $('p.time_expired.allowed', $('#editCommentPopup')).css('display', 'none').text('');
    }
};

$(function () {
    'use strict';
    EditCommentBoxHandler.activate();
});/*globals $,ReplyBoxHandler,OverlayHandler,CommentsHandler./

/* substitute for the alert modal windows*/
var PageNotifier = {
    callbackOnClose : null,
    activate : function () {
        'use strict';
        var that = this;
        $('#messagePopup .close_popup,#messagePopup .reset-reply').click(that.hideNotificationPopup);
    },

    showNotificationPopup : function (message, callbackOnClose, parent) {
        'use strict';
        this.callbackOnClose = callbackOnClose;
        var messagePopup = $('#messagePopup');
        $('p.allowed', messagePopup).html(message);
        if (parent !== undefined && parent.length > 0 && parent.is(":visible")) {
            messagePopup.detach().appendTo(parent);
        } else {
            messagePopup.detach().appendTo($('.comment__form'));
        }
        messagePopup.show();
    },

    hideNotificationPopup : function () {
        'use strict';
        var messagePopup = $('#messagePopup');
        messagePopup.detach().appendTo($('.comment__form'));
        messagePopup.hide();
        CommentsPoster.enableForumPost("submitComment", "comment_here");

        if (PageNotifier.callbackOnClose) {
            PageNotifier.callbackOnClose.call();
        }
    }
};

$(function () {
    'use strict';
    PageNotifier.activate();
});/*when user submits html content, only some tags allowed, the rest escaped */
var Parser = {
    fixTags : function (text) {
        'use strict';
        var tagStack = [],
            output = [],
            openTag = false,
            closeTag = false,
            tagChars = [],
            length = text.length,
            tagName,
            ch = '',
            i, // iterator
            fullTagName,
            alreadyClosed,
            spaceIdx,
            lowerCaseTagName,
            lastEl,
            tag;
        for (i = 0; i < length; i++) {
            ch = text.charAt(i);
            if (((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z'))) {
                if (openTag || closeTag) {
                    tagChars.push(ch);
                } else {
                    output.push(ch);
                }
                continue;
            }
            if (ch === '<') {
                if (openTag || closeTag) { // flush the accumulated chars as this is possibly a tag opener
                    output.push(tagChars.join(""));
                    tagChars = [];
                }
                if (i + 2 < length && text.charAt(i + 1) !== " ") {
                    if (text.charAt(i + 1) === "/") {
                        i++;
                        if (i + 2 < length && text.charAt(i + 1) !== " ") {
                            closeTag = true;
                            tagChars.push("</");
                        } else {
                            output.push("</");
                        }
                    } else {
                        openTag = true;
                        tagChars.push("<");
                    }
                } else {
                    output.push(ch);
                }
                continue;
            }
            if (ch === '>') {
                if (openTag) {
                    // found a tag
                    fullTagName = tagChars.join("");
                    alreadyClosed = false;
                    if ("/" === text.charAt(i - 1)) {
                        // see if it is already closed
                        fullTagName = fullTagName.substring(0, fullTagName.length - 1);
                        alreadyClosed = true;
                    }
                    spaceIdx = fullTagName.indexOf(" ");
                    if (spaceIdx === -1) { // tag name only
                        tagName = fullTagName.substring(1);
                    } else { // tag with attributes
                        tagName = fullTagName.substring(1, spaceIdx);
                    }
                    lowerCaseTagName = tagName.toLowerCase();
                    if (alreadyClosed) {
                        output.push(fullTagName + "/>");
                    } else if ("br" === lowerCaseTagName) {
                        // br doesn't need a closing tag
                        output.push("<br/>");
                    } else {
                        tagStack.push(tagName.toLowerCase());
                        output.push(fullTagName + ">");
                    }
                    tagChars = [];
                    openTag = false;
                } else if (closeTag) {
                    // found close tag
                    tagName = tagChars.join("").substring(2);
                    tagChars = [];
                    closeTag = false;
                    // check if the tag is in the stack
                    if (tagStack.length > 0) {
                        lastEl = tagStack[tagStack.length - 1];
                        if (lastEl === tagName.toLowerCase()) {
                            tagStack.pop();
                        } else if ("br" !== tagName) {
                            output.push("<" + tagName + ">");
                        }
                    } else if ("br" !== tagName) {
                        output.push("<" + tagName + ">");
                    }
                    if ("br" === tagName) {
                        output.push("<br/>");
                    } else {
                        output.push("</" + tagName + ">");
                    }
                } else {
                    output.push(ch);
                }
                continue;
            }
            if (openTag) {
                tagChars.push(ch);
            } else if (closeTag) { // close tags do not allow attribute
                closeTag = false;
                output.push(tagChars.join(""));
                tagChars = [];
            } else {
                output.push(ch);
            }
        }
        if (tagChars.length > 0) {
            output.push(tagChars.join(""));
        }
        while (tagStack.length > 0) {
            tag = tagStack.pop();
            output.push("</" + tag + ">");
        }
        return output.join("");
    }
};/*global $,CommentsHandler.CommentsPoster./
/* takes care of reply box UI */
var ReplyBoxHandler = {

    replyBoxAnchor : null, // in case reply link is clicked, the reply box will be positioned relative to this element

    activate : function () {
        'use strict';
        this.activateClose();
        this.activatePostButton();
        this.activateCancelButton();
    },

    // register the reply box close event
    activateClose : function () {
        'use strict';
        var that = this;
        $('#replyPopup>.close_popup').click(function () {
            that.hideReplyBox();
        });
    },

    activatePostButton : function () {
        'use strict';
        $('#submit-reply').click(CommentsPoster.postComment);
    },

    activateCancelButton : function () {
        'use strict';
        var that = this;
        $('.reset-reply', $('#replyPopup')).click(function () {
            that.hideErrorMessage();
            that.hideReplyBox();
        });
    },

    showReplyBox : function () {
        'use strict';
        var replyBox = $('#replyPopup');
        EditCommentBoxHandler.hideEditCommentBox();

        // solve positioning
        $(replyBox).show();
        // construct the subject if it's a reply. Don't if the comment originates in 'leave a comment'
        if (CommentsHandler.referenceCommentId > 0) {
            var subject = $('.subject', replyBox);
            subject.val(ReplyBoxHandler.computedSubject());
            subject.select();
        }
        replyBox.find('input[name=emailMe]').attr('checked', forumNotificationDefault);
        $('.commentsReply').val("");
        $('.commentsReply', replyBox).focus();

        // in case we came here from force close the popup
        $.colorbox.close();
    },

    hideReplyBox : function () {
        'use strict';
        var replyBox = $('#replyPopup');
        replyBox.detach().appendTo(".comments__boxes");
        replyBox.hide();
    },

    clearReplyBox : function () {
        'use strict';
        var replyBox = $('#replyPopup');
        $('.subject', replyBox).val('');
        $('textarea.commentsReply', replyBox).val('');
        this.hideErrorMessage();
    },

    focusOnSubject : function () {
        'use strict';
        $('.subject', $('#replyPopup')).focus();
    },

    computedSubject : function () {
        'use strict';
        var corrFlatComment = $('#anch' + CommentsHandler.referenceCommentId).first(),
            result = $('.comment__title', corrFlatComment).text();
        // append a Re: in front, only if it's not already starting with it
        if (result.substring(0, JSi18n.re.length) !== JSi18n.re) {
            result = JSi18n.re + ' ' + result;
        }
        // trim the subject to 75 characters
        if (result.length > 75) {
            result = result.substring(0, 75);
        }
        return result;
    },

    showErrorMessage : function (errorMessage) {
        'use strict';
        $('p.error.allowed', $('#replyPopup')).css('display', 'block').text(errorMessage);
        setTimeout(ReplyBoxHandler.hideErrorMessage, 3000);
    },

    hideErrorMessage : function () {
        'use strict';
        $('p.error.allowed', $('#replyPopup')).css('display', 'none').text('');
    },

    inviteToSignIn: function() {
        ReplyBoxHandler.showReplyBox();
        UserActions_Login.showLoginWidgetRightSide($('#replyPopup'), 'replyBoxLogin');
    }
};

$(function () {
    'use strict';
    ReplyBoxHandler.activate();
});/*global $,PageNotifier*/

/* takes care of the watch thread/unwatch thread functionality */
var watcher = {
    activate : function () {
        'use strict';
        this.setLinkText();
        this.activateLink();
    },

    setLinkText : function () {
        'use strict';
        try{
	        if (threadWatched) {
	            $('#watch').text(JSi18n.stopWatchText);
	        } else {
	            $('#watch').text(JSi18n.startWatchText);
	        }
        }catch(err){}

    },

    watchThread : function() {
    	$.ajax({
            url: threadWatched ? postRemoveWatches : postAddWatches,
            type: 'POST',
            data: {
                'forumID': forumID,
                'threadID': threadID
            },
            complete: function (e) {
                if (e.status === 200) {
                    threadWatched = !threadWatched;
                    watcher.setLinkText();
                } else if (e.status === 401) {
                    UserActions_Login.showLoginWidget($('#watch'), 'watchForumThread');
                } else {
                    PageNotifier.showNotificationPopup(JSi18n.error); // reasonable fallback
                }
            }
        });
    	$.colorbox.close();
    },
    
    activateLink : function () {
        'use strict';
        $('#watch').click(function (event) {
            if(!loggedIn) {
                event.preventDefault();
                infoq.event.trigger('login', { ref: 'watchForumThread' });
            } else {
                UserActions_Profile.forceUpdateProfile(watcher.watchThread);
            }
        });
    }
};

$(function () {
    'use strict';
    watcher.activate();
});