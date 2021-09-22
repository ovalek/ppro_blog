function isSet( variable )
{
    return( typeof( variable ) != 'undefined' );
}

function fill(str, max) {
    str = str.toString();
    return str.length < max ? fill("0" + str, max) : str;
}

function isTouchDevice(){
    return true == ("ontouchstart" in window || window.DocumentTouch && document instanceof DocumentTouch);
}



(function ($) {
    $.fn.inputChange = function(callback, ms){
        var timer = 0;
        $(this).on('input keydown change', function(event) {
            clearTimeout (timer);
            timer = setTimeout(function(event) {
                callback(event);
            }, ms);
        });
        return $(this);
    };
})(jQuery);



// add window resize end event
$(window).resize(function() {
    if(this.resizeTO) clearTimeout(this.resizeTO);
    this.resizeTO = setTimeout(function() {
        $(this).trigger('resizeEnd');
    }, 500);
});



window.dialogs = [];

jQuery(document).ready(function($) {

    var basePath = $('body').attr('data-basepath');
    window.basePath = basePath;

    $.notifyDefaults({
        type: 'info',
        newest_on_top: true,
        z_index: 1000019,
        delay: 10000,
        mouse_over: 'pause',
        placement: {
                from: "top",
                align: "center"
        },
        animate: {
                enter: 'animated fadeInDown',
                exit: 'animated fadeOutRight'
        }
    });
    
    
    
    /** Init tooltips */
    if (isTouchDevice() === false) {
        $('[data-toggle="tooltip"]').tooltip();
    }



    jQuery.extend({
        my: {
            checkResponse: function(json) {
                if (typeof json.redirect != undefined && json.redirect != undefined) { window.location = json.redirect; return false; }
                return json;
            },
            notify: function(msg, type, options, settings) {
                if (!isSet(options)) {
                    var options = {};
                }
                if (!isSet(settings)) {
                    var settings = {};
                }

                if (!isSet(settings.z_index)) {
                    settings.z_index = 1000019;1000019
                }
                options.message = msg;
                
                if (isSet(type)) {
                    if (type === 'error' || type === 'fatalError') {
                        settings.type = 'danger';
                        settings.delay = 0;
                    }
                    else if (type === "warn" || type === "alert") settings.type = 'warning';
                    else settings.type = type;
                }
                
                $.notify(options, settings);
            },
            /**
             * Confirmation dialog with OK and Storno callback
             * @param {string} dialogName Dialog name in window.dialogs array.
             * @param {string} type Type of dialog - danger, warning, success, primary, info, default.
             * @param {string} title Dialog title.
             * @param {string} question Dialog content.
             * @param {string} closeDialogBeforeCallback Whether close the dialog before callback or not - both, ok, storno, never (or anything).
             * @param {callback} callbackOk Function to execute if the question was accepted.
             * @param {callback} callbackStorno Function to execute if the question was rejected or user close the dialog.
             */
            confirmDialogWC: function(dialogName, type, title, question, closeDialogBeforeCallback, callbackOk, callbackStorno) {
                var types = {
                    "default":BootstrapDialog.TYPE_DEFAULT,
                    "info":BootstrapDialog.TYPE_INFO,
                    "primary":BootstrapDialog.TYPE_PRIMARY,
                    "success":BootstrapDialog.TYPE_SUCCESS,
                    "ok":BootstrapDialog.TYPE_SUCCESS,
                    "warning":BootstrapDialog.TYPE_WARNING,
                    "danger":BootstrapDialog.TYPE_DANGER
                };
                if (!type in types) type = "primary";
                if (type == "ok") type = "success";
                
                window.dialogs[dialogName] = BootstrapDialog.show({
                    type: types[type],
                    title: title,
                    message: question,
                    onhide: function(dialog) {
                        if (dialog.rip === true) {
                            return true;
                        } else {
                            if (closeDialogBeforeCallback !== "both" && closeDialogBeforeCallback !== "storno") {
                                return false;
                            } else {
                                dialog.rip = true;
                                dialog.close();
                                if (isSet(callbackStorno)) {
                                    callbackStorno(dialog);
                                }
                            }
                        }
                    },
                    buttons: [{
                        label: 'OK',
                        cssClass: 'btn-'+type,
                        action: function(dialog) {
                            if (closeDialogBeforeCallback === "both" || closeDialogBeforeCallback === "ok") {
                                dialog.rip = true;
                                dialog.close();
                            }
                            if (isSet(callbackOk)) {
                                callbackOk(dialog);
                            }
                        }
                    }, {
                        label: 'Storno',
                        action: function(dialog) {
                            dialog.close();
                        }
                    }]
                });
            },
            getDialogLoader: function(msg) {
                if (!isSet(msg)) msg = 'Probíhá načítání';
                return $('<div class="progress dialogLoader"><div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%">' + msg + '</div></div>');
            },
//            getValidationLoader: function(msg) {
//                if (!isSet(msg)) msg = 'Probíhá validace';
//                return $('<div class="validationLoader" title="' + msg + '"><span class="glyphicon glyphicon-refresh glyphicon-refresh-animate"></span></div>');
////                return $('<div class="progress validationLoader" title="' + msg + '"><div class="progress-bar progress-bar-warning progress-bar-striped active" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div></div>');
//            },
//            confirmDialog: function(dialogName, type, title, question, location) {
//                $.my.confirmDialogWC(dialogName, type, title, question, "storno", function(){
//                    window.location.replace(location);
//                }, function(){
//                    window.dialogs[dialogName].close();
//                });
//            }
            formLoad : function($selector, disableAutoFocus) {
                
                $selector = (typeof $selector === "undefined") ? $('body') : $selector;
                
//                for (var i = 0; i < $selector.forms.length; i++) {
//                    Nette.initForm($selector.forms[i]);
//                }
                
                $selector.find('form').each(function(index, element) {
                    Nette.initForm(element);
                })
                
                $selector.find(".urlPartCharsOnly").filter_input({regex:'[a-z0-9\-\/]'});
                $selector.find(".nonNegativeNumber").filter_input({regex:'[0-9]'});
                $selector.find("form[data-live-validation-url]").myLiveValidate($(this).attr('data-live-validation-events'));
                
                $selector.find('.form-control[data-use-select2]').each(function(index, element) {
                    var $select = $(element);
                    if ($select.attr('data-use-select2').length) {
                        $select.select2({data: $.parseJSON($select.attr('data-use-select2'))});
                    } else {
                        $select.select2();
                    }
                    // hack (refresh form)
                    setTimeout(function(){
                        $select.trigger('change');
                    }, 500);
                });

				if (jQuery().datetimepicker) {
					$selector.find('.addDatePicker').datetimepicker({
						locale: 'cs',
						maxDate: new Date(),
						format: 'YYYY-MM-DD',
						useStrict: true
					});
				}

				if (jQuery().ckeditor) {
					var $tmp = $selector.find('.addCkeditor');
					if ($tmp.length) {
						$tmp.ckeditor($.parseJSON($tmp.attr('data-ck-config')));
					}
				}
                
                if (isTouchDevice() === false) {
                    $selector.find('[data-toggle="tooltip"]').tooltip();
                }
                
                if (!(isSet(disableAutoFocus) || disableAutoFocus === true)) {
                    setTimeout(function() {
                        if (($selector).is('.modal-body')) {
                            $selector.closest('.bootstrap-dialog').on('shown.bs.modal', function () {
                                $selector.find("form :input:visible:enabled:first").focus();
                            });
                        } else {
                            $selector.find("form :input:visible:enabled:first").focus();
                        }
                    }, 150);
                }
            },
            extractCssUrl: function(input) {
                return input.replace(/"/g,"").replace(/url\(|\)$/ig, "");
            },
            urlStringAddTimestamp: function(url) {
                var timestamp = new Date().getTime();
                //  if (url.indexOf("jsRefreshTimestamp") >= 0) {
                if (/refreshTimestamp/.test(url)) {
                    //  photoURL = photoURL.substring(-1, photoURL.length - 13);
                    url = url.slice(0, url.lastIndexOf("=")+1) + timestamp;
                    //  } else if (url.indexOf("?") >= 0) {
                } else if (/[?]/.test(url)) {
                    url = url + "&amp;refreshTimestamp=" + timestamp;
                } else {
                    url = url + "?refreshTimestamp=" + timestamp;
                }
                return url;
            },
        }
    });
});



// get element's depth
(function ($) {
    $.fn.depth = function(filter){
        var root = $(this)[0];
        filter = typeof filter !== 'undefined' ? filter : '*';
        var depth = 0;
        $(root).find(':not(:has('+filter+'))').each(function() {
            if (filter == '*') {
                var tmp = $(this).parentsUntil(root, filter).length + 1;
            } else {
                var tmp = $(this).parentsUntil(root, filter).length;
            }
            if (tmp > depth) depth = tmp;
        });

        return depth;
    }
})(jQuery);



// live form validation
(function ($) {
    $.fn.myLiveValidate = function(customEvents){
        customEvents = (typeof customEvents === "undefined") ? null : customEvents;
        
        for (var i = 0; i < $(this).length; i++) {
            var form = $($(this)[i]);
            var inputs = form.find('[data-live-validate]');
            if (form.is('[data-live-validation-url]') && !form.is('[data-live-validation-attached]') && inputs.length > 0) {
                form.attr('data-live-validation-attached');
                                                
                var helpBlocksIDs = false;
                                
                // item status help-block
                for (var j = 0; j < inputs.length; j++) {
                    var $input = $(inputs[j]);
//                    $input.addClass('hasLiveValidation');
                    if (!$("#" + $input.attr('id') + '_status').length) {
                        if ($input.parent().is('label')) {
                            $input.parent().parent().addClass('hasLiveValidation').after('<div class="validationStatus" id="' + $input.attr('id') + '_status" data-input-name="' + $input.attr('name') + '"></div>');
                        } else {
                            $input.addClass('hasLiveValidation').after('<div class="validationStatus" id="' + $input.attr('id') + '_status" data-input-name="' + $input.attr('name') + '"></div>');
                        }
                        if (helpBlocksIDs === false) {
                            helpBlocksIDs = '#' + $input.attr('id') + '_status';
                        } else {
                            helpBlocksIDs = helpBlocksIDs + ', #' + $input.attr('id') + '_status';
                        }
                    }
                }
                
                
                var goValidate = function() {
                    $(helpBlocksIDs).removeClass('valid error').html('<span title="Probíhá validace" class="glyphicon glyphicon-refresh glyphicon-refresh-animate"></span>');
                    $.ajax({
                        type: "POST",
                        url: form.attr('data-live-validation-url'),
                        data: "serializedMainFields=" + encodeURIComponent(form.find("[data-live-validate]").serialize()) + "&serializedAdditionalFields=" + encodeURIComponent(form.find("[data-use-for-validation]").serialize()),
                        success: function(response){
                            var json = $.my.checkResponse(response);
                            if (json.status) {
                                $(helpBlocksIDs).each(function() {
                                    var $helpBlock = $(this);

                                    // if we have a result for some of the validatedInputs
                                    if (json.validationResults.hasOwnProperty($helpBlock.attr('data-input-name'))) {
                                        // if validation succeded, otherwise...
                                        if (json.validationResults[$helpBlock.attr('data-input-name')]) {
                                            $helpBlock.closest('.form-group').removeClass('has-error').addClass('has-success');
                                            LiveForm.removeError($helpBlock.closest('.form-group').find('[name=' + $helpBlock.attr('data-input-name') + ']')[0]);
                                            
                                            if (json.validationMessages.hasOwnProperty($helpBlock.attr('data-input-name'))) {
    //                                            $helpBlock.addClass('valid').text(json.validationMessages[$helpBlock.attr('data-input-name')]);
                                                $helpBlock.addClass('valid').html('<span title="' + $('<div/>').html(json.validationMessages[$helpBlock.attr('data-input-name')]).text() + '" class="glyphicon glyphicon-ok"></span>');
                                            } else {
                                                $helpBlock.addClass('valid').html('<span class="glyphicon glyphicon-ok"></span>');
                                            }
                                        } else if (!json.validationResults[$helpBlock.attr('data-input-name')] && json.validationMessages.hasOwnProperty($helpBlock.attr('data-input-name'))) {
                                            $helpBlock.closest('.form-group').removeClass('has-success').addClass('has-error');
//                                            $helpBlock.removeClass('valid').text(json.validationMessages[$helpBlock.attr('data-input-name')]);
                                            $helpBlock.removeClass('valid').addClass('error').html('<span title="' + $('<div/>').html(json.validationMessages[$helpBlock.attr('data-input-name')]).text() + '" class="glyphicon glyphicon-exclamation-sign"></span>');
                                            LiveForm.addError($helpBlock.closest('.form-group').find('[name=' + $helpBlock.attr('data-input-name') + ']')[0], json.validationMessages[$helpBlock.attr('data-input-name')])
                                        } else {
                                            $helpBlock.closest('.form-group').removeClass('has-error').addClass('has-success');
                                            $helpBlock.removeClass('valid error').empty();
                                            LiveForm.removeError($helpBlock.closest('.form-group').find('[name=' + $helpBlock.attr('data-input-name') + ']')[0]);
                                        }
                                    } else {
                                        $helpBlock.closest('.form-group').removeClass('has-error').removeClass('has-success');
                                        $helpBlock.removeClass('valid error').empty();
                                        LiveForm.removeError($helpBlock.closest('.form-group').find('[name=' + $helpBlock.attr('data-input-name') + ']')[0]);
                                    }
                                });
                            } else {
                                $(helpBlocksIDs).each(function(index, helpBlock) {
                                    $(helpBlock).closest('.form-group').removeClass('has-error').removeClass('has-success');
                                    $(helpBlock).removeClass('valid error').html('<span title="Došlo k chybě při zpracovávání požadavku." class="glyphicon glyphicon-question-sign"></span>');
                                    LiveForm.removeError($(helpBlock).closest('.form-group').find('[name=' + $(helpBlock).attr('data-input-name') + ']')[0]);
                                });
                            }
                        },
                        error: function(){
                            $(helpBlocksIDs).each(function(index, helpBlock) {
                                $(helpBlock).closest('.form-group').removeClass('has-error').removeClass('has-success');
                                $(helpBlock).removeClass('valid error').html('<span title="Chyba při komunikaci se serverem." class="glyphicon glyphicon-question-sign"></span>');
                                LiveForm.removeError($(helpBlock).closest('.form-group').find('[name=' + $(helpBlock).attr('data-input-name') + ']')[0]);
                            });
                        }
                    });
                };
                
                // bind live validation
                var enableValidation = true;
                form.find('[data-use-for-validation],[data-live-validate]').inputChange(function()  {
                    if (enableValidation) {
                        setTimeout(function() {
                            goValidate();
                            enableValidation = true;
                        }, 0);
                        enableValidation = false;
                    }
                }, 500);
                if (isSet(customEvents)) {
                    form.find('[data-use-for-validation],[data-live-validate]').on(customEvents, function() {
                        if (enableValidation) {
                            setTimeout(function() {
                                goValidate();
                                enableValidation = true;
                            }, 500);
                            enableValidation = false;
                        }
                    });
                }
            }
        }

        return $(this);
    };
})(jQuery);



jQuery(document).ready(function($) {
    $.my.formLoad();
});