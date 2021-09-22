/**
 * @license Copyright (c) 2015, Oldřich Válek (oldrich<dot>valek<at>gmail<dot>com)
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

CKEDITOR.plugins.add('imagePlupload', {
    icons: false,
    lang: 'cs',
    requires: 'image,link',
    init: function (editor) {

        var lang = editor.lang.imagePlupload;
        console.log('init');
//        console.log(editor.config.imagePlupload_url);
//        console.log(editor.config.imagePlupload_preUploadWidth);
//        console.log(editor.config.imagePlupload_preUploadHeight);
//        console.log(editor.config.imagePlupload_preUploadQuality);
//        console.log(editor.config.imagePlupload_flash_swf_url);
//        console.log(editor.config.imagePlupload_silverlight_xap_url);

        //if (!CKEDITOR.dialog.isTabEnabled( editor, 'image', 'upload' )) return;

        if (isSet(window.imagePluploadUsedIn)) {
            window.imagePluploadUsedIn = jQuery.grep(window.imagePluploadUsedIn, function (value) {
                return value != editor.name;
            });
        }

        var idPrefix = CKEDITOR.tools.getNextId() + "_imagePlupload_";
        
        /** Plupload */
        var uploader;

        /** Default values for the first image dialog initialization */
        var maxThumbWidth = 300;
        var maxThumbHeight = 300;
        var thumbOnly = false;
        
        if (
            isSet(editor.config.imagePlupload_url) &&
            isSet(editor.config.imagePlupload_preUploadWidth) &&
            isSet(editor.config.imagePlupload_preUploadHeight) &&
            isSet(editor.config.imagePlupload_preUploadQuality)) {

            /** Probably not neccesary here */
            editor.filter.allow({
                a: {
                    attributes: 'href',
                    propertiesOnly: true
                },
                img: {
                    attributes: 'src',
                    propertiesOnly: true
                }
            }, 'formRequired');
            //CKEDITOR.on('Ok', function(evt) {
            //CKEDITOR.has
            //CKEDITOR.config.removeDialogTabs;
            CKEDITOR.on('dialogDefinition', function (evt) {
                var data = evt.data;

                //if (!isSet(window.imagePluploadUsedIn)) window.imagePluploadUsedIn = [];
                if (data.name == 'image'/* && window.imagePluploadUsedIn.indexOf(evt.editor.name) == -1*/) {
                    //window.imagePluploadUsedIn.push(evt.editor.name);

                    // Get dialog definition.
                    var def = evt.data.definition;

                    if(def.contents.length >=3) {
                            def.minWidth=460
                    }

                    var dialog = evt.data.definition.dialog;

                    if (!isSet(window.imagePluploadCounter)) window.imagePluploadCounter = 0;

                    window.imagePluploadCounter++;
                    console.log(window.imagePluploadCounter);
                    //return;

                    //console.log(def.getContents('imagePlupload'));
                    //if (def.getContents('imagePlupload')) {
                    //    def.removeContents('imagePlupload');
                    //} /*else {*/
                    if (!isSet(window.imagePluploadUsedIn)) window.imagePluploadUsedIn = [];
                    if (window.imagePluploadUsedIn.indexOf(evt.editor.name) == -1) {
                        window.imagePluploadUsedIn.push(evt.editor.name);
                        def.addContents({
                            id: 'imagePlupload',
                            label: lang.tabLabel,
                            elements: [
                                {
                                    type: 'hbox',
                                    requiredContent: 'img{width,height}',
                                    widths: ['40%', '12%', '3%', '45%'],
                                    children: [
                                        {
                                            type: 'vbox',
                                            padding: 1,
                                            children: [
                                                {
                                                    type: 'html',
                                                    html: '<span style="line-height: 4.2;">' + lang.maxThumbSize + ':</span>'
                                                },
                                            ]
                                        },
                                        {
                                            type: 'vbox',
                                            padding: 1,
                                            children: [
                                                {
                                                    type: 'text',
                                                    width: '45px',
                                                    id: 'maxThumbWidth',
                                                    label: editor.lang.common.width,
                                                    'default': maxThumbWidth,
                                                    onChange: function () {
                                                        if (this.validate()) {
                                                            maxThumbWidth = this.getValue();
                                                        } else {
                                                            this.setValue(maxThumbWidth);
                                                        }
                                                    },
                                                    validate: function () {
                                                        var aMatch = this.getValue().match(/^\s*(\d+)((px)|\%)?\s*$/i),
                                                            isValid = !!(aMatch && parseInt(aMatch[1], 10) !== 0);
                                                        if (!isValid)
                                                            alert(editor.lang.common.invalidWidth); // jshint ignore:line
                                                        return isValid;
                                                    },
                                                },
                                            ]
                                        },
                                        {
                                            type: 'vbox',
                                            padding: 1,
                                            children: [
                                                {
                                                    type: 'html',
                                                    width: '10px',
                                                    html: '<strong style="line-height: 4;">X</strong>'
                                                }
                                            ]
                                        },
                                        {
                                            type: 'vbox',
                                            padding: 1,
                                            children: [
                                                {
                                                    type: 'text',
                                                    id: 'maxThumbHeight',
                                                    width: '45px',
                                                    label: editor.lang.common.height,
                                                    'default': maxThumbHeight,
                                                    onChange: function () {
                                                        if (this.validate()) {
                                                            maxThumbHeight = this.getValue();
                                                        } else {
                                                            this.setValue(maxThumbHeight);
                                                        }
                                                    },
                                                    validate: function () {
                                                        var aMatch = this.getValue().match(/^\s*(\d+)((px)|\%)?\s*$/i),
                                                            isValid = !!(aMatch && parseInt(aMatch[1], 10) !== 0);
                                                        if (!isValid)
                                                            alert(editor.lang.common.invalidHeight); // jshint ignore:line
                                                        return isValid;
                                                    },
                                                }
                                            ]
                                        },

                                    ]
                                },
                                {
                                    type: 'hbox',
                                    padding: 1,
                                    children: [
                                        {
                                            type: 'checkbox',
                                            id: 'thumbOnly',
                                            label: lang.thumbOnly,
                                            'default': false,
                                            onChange: function () {
                                                thumbOnly = this.getValue();
                                            },
                                        }
                                    ]
                                },
                                {
                                    type: 'html',
                                    html: '<div id="' + idPrefix + 'container" style="float:left; margin:20px 15px 20px 0;"><a id="' + idPrefix + 'pickFileBtn" class="cke_dialog_ui_button cke_dialog_ui_button_ok" role="button" hidefocus="true" href="javascript:;"><span class="cke_dialog_ui_button" style="color:#fff;font-weight:bold;">' + lang.pickFileBtn + '</span></a></div><div id="' + idPrefix + 'fileInfo" style="margin:25px 0 15px 15px;">Your browser doesn\'t have Flash, Silverlight or HTML5 support.</div>'
                                },
                                {
                                    id: 'uploadFile',
                                    type: 'button',
                                    label: lang.submitLabel,
                                    style: 'visibility:hidden; opacity:0.0;',
                                    disabled: false,
                                    onClick: function () {
                                        if (uploader.files.length === 1) {
                                            uploader.settings.url = editor.config.imagePlupload_url + "&thumbMaxWidth=" + maxThumbWidth + "&thumbMaxHeight=" + maxThumbHeight + "&thumbOnly=" + thumbOnly;
                                            uploader.start();
                                        } else {
                                            alert(lang.pickFile);
                                        }
                                        return false;
                                    }
                                },
                                {
                                    type: 'html',
                                    html: '<div id="' + idPrefix + 'progress" style="opacity:0.0; height:20px; line-height:18.1818180084229px; display:block; margin:20px 0; overflow:hidden; background-color:rgb(245, 245, 245); border-bottom-left-radius:4px; border-bottom-right-radius:4px; border-top-left-radius:4px; border-top-right-radius:4px; box-shadow:rgba(0, 0, 0, 0.0980392) 0px 1px 2px 0px inset; box-sizing:border-box;"><div id="' + idPrefix + 'progressBar" style="height:20px; width:0%; line-height:20px; box-sizing:border-box; float:left; background-color:rgb(51, 122, 183); background-image:linear-gradient(45deg, rgba(255, 255, 255, 0.14902) 25%, transparent 25%, transparent 50%, rgba(255, 255, 255, 0.14902) 50%, rgba(255, 255, 255, 0.14902) 75%, transparent 75%, transparent); background-size:40px 40px; box-shadow:rgba(0, 0, 0, 0.14902) 0px -1px 0px 0px inset; transition:width 0.3s ease-in-out; animation:progress-bar-stripes 2s linear infinite;" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div></div>'
                                }
                            ]
                        });
                    }



                    /** Whether the uploader should refresh or not */
                    var refresh = false;



                    var initUploader = function() {
                        console.log('initUploader');

                        uploader = new plupload.Uploader({
    //                        runtimes: 'html5,flash,silverlight,html4', I wasn't able to make flash and silverlight working properly after first upload.
                            runtimes: 'html5,html4',
                            browse_button: idPrefix+'pickFileBtn',
                            container: document.getElementById(idPrefix+'container'),
                            drop_element: document.getElementById(idPrefix+'container'),
                            url: editor.config.imagePlupload_url,
                            max_retries: 2,
                            multi_selection: false,
                            chunk_size: '1000kb',
                            unique_names : false,
                            filters: {
                                max_file_size: '20mb',
                                mime_types: [
                                    {title: "Image files", extensions: "jpg,png"}
                                ]
                            },
                            resize: {
                                width: editor.config.imagePlupload_preUploadWidth,
                                height: editor.config.imagePlupload_preUploadHeight,
                                quality: editor.config.imagePlupload_preUploadQuality,
                                crop: false, // crop to exact dimensions
                                preserve_headers: false
                            },
                            // Flash settings
//                            flash_swf_url: editor.config.imagePlupload_flash_swf_url,
                            // Silverlight settings
//                            silverlight_xap_url: editor.config.imagePlupload_silverlight_xap_url,
                            init: {
                                FilesAdded: function(up, files) {
                                    while (up.files.length > 1) {
                                        up.removeFile(up.files[0]);
                                    }

                                    plupload.each(files, function (file) {
                                        document.getElementById(idPrefix+'fileInfo').innerHTML = '<div id="' + idPrefix + file.id + '">' + file.name + ' (' + plupload.formatSize(file.size) + ') <b></b></div>';
                                    });

    //                                jQuery('#'+dialog.getContentElement('imagePlupload', 'uploadFile').domId).css('visibility', 'visible').fadeTo(150, '1.0');

                                    uploader.settings.url = editor.config.imagePlupload_url + "&maxThumbWidth=" + maxThumbWidth + "&maxThumbHeight=" + maxThumbHeight + "&thumbOnly=" + thumbOnly;
                                    uploader.start();
                                },
                                UploadFile: function (up, file) {
                                    jQuery('#'+idPrefix+'progress').fadeTo(150, '1.0');
                                },
                                UploadProgress: function (up, file) {
                                    jQuery('#'+idPrefix+'progressBar').css('width', file.percent + '%');
                                    document.getElementById(idPrefix + file.id).getElementsByTagName('b')[0].innerHTML = '<span>' + file.percent + "%</span>";
                                },
                                FileUploaded: function (up, file, response) {
                                    console.log(response.response);
                                    var json = jQuery.parseJSON(response.response);
                                    if (json != null && typeof json.redirect != undefined && json.redirect != undefined) { window.location = json.redirect; return FALSE; }
                                    if (json != null && isSet(json.status) && json.status && isSet(json.thumbUrl) && isSet(json.imageUrl) && isSet(json.thumbWidth) && isSet(json.thumbHeight)) {
                                        dialog.getContentElement( 'info', 'txtUrl' ).setValue( json.thumbUrl );
                                        dialog.getContentElement( 'info', 'txtWidth' ).setValue( json.thumbWidth );
                                        dialog.getContentElement( 'info', 'txtHeight' ).setValue( json.thumbHeight );
                                        dialog.getContentElement( 'Link', 'txtUrl' ).setValue( json.imageUrl );

                                        up.state = plupload.DONE;

                                        beforeClose();

                                        dialog.selectPage( 'info' );
                                    } else {
                                        alert(lang.badResponse);

                                        beforeClose();
                                    }
                                },
                                Error: function (up, err) {
                                    uploader.refresh();
                                    beforeClose();

                                    console.log("Error #" + err.code + ": " + err.message);

                                    alert(err.message);
                                }
                            }
                        });
                        uploader.init();
                    };



                    var beforeClose = function() {
                        if (!refresh) {
                            refresh = true;

                            if (isSet(uploader)) {
                                uploader.splice();
                            }

                            document.getElementById(idPrefix+'fileInfo').innerHTML = '';
                            jQuery('#'+idPrefix+'progress').fadeTo(0, '0.0', function() {
                                jQuery('#'+idPrefix+'progressBar').css('width', '0%');
                            });
    //                        jQuery('#'+dialog.getContentElement('imagePlupload', 'uploadFile').domId).css({'visibility':'hidden', 'opacity':'0.0'});
                        }
                    };



                    var preventClosing = function() {
                        if (isSet(uploader)) {
                            if (uploader.state === plupload.UPLOADING) {
                                if (confirm(lang.confirmStop)) {
                                    uploader.stop();
                                    beforeClose();
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                beforeClose();
                            }
                        }
                        return true;
                    };



                    dialog.on("cancel", function(event) {
                        if (event.data.hide) {
                            event.data.hide = preventClosing();
                        }
                    });

                    dialog.on("ok", function(event) {
                        if (event.data.hide) {
                            event.data.hide = preventClosing();
                        }
                    });



                    /** When page (dialog tab) is being changed */
                    dialog.on("selectPage", function(event) {
                        if (event.data.currentPage === "imagePlupload" && event.data.page !== "imagePlupload") {
                            return preventClosing();
                        } else if (event.data.page === "imagePlupload") {

                            dialog.getContentElement( 'imagePlupload', 'maxThumbWidth' ).setValue( maxThumbWidth );
                            dialog.getContentElement( 'imagePlupload', 'maxThumbHeight' ).setValue( maxThumbHeight );
                            document.getElementById(idPrefix+'fileInfo').innerHTML = '';

                        }
                    });



                    /** Since IE, flash and silverlight sucks and CKEditor does not have event for page/tab selected or shown, there has to be this ugly bullshit. */
                    dialog.on("show", function() {
                        var tabDomId = jQuery('#'+dialog.getContentElement( 'imagePlupload', 'uploadFile' ).domId).closest('[name="imagePlupload"]').attr('id');
                        jQuery("body").on('mouseover focusin keydown', '#'+tabDomId , function(event) {
                            if (!isSet(uploader)) {
                                initUploader();
                                refresh = false;
                            } else if (refresh) {
                                uploader.refresh();
                                refresh = false;
                            }
                        });
                    });

                }
            });
        }
    }
});