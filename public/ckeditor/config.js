/**
 * @license Copyright (c) 2003-2015, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */
//CKEDITOR.config.extraPlugins = 'imagePlupload,notification,autosave';
CKEDITOR.config.extraPlugins = 'notification,autosave';
CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here.
	// For complete reference see:
	// http://docs.ckeditor.com/#!/api/CKEDITOR.config

	config.contentsCss = [
		CKEDITOR.getUrl( window.basePath + '/assets/bootstrap/css/bootstrap.paper.min.css' ),
		CKEDITOR.getUrl( window.basePath + '/assets/css/contents.css' )
	];

	config.customConfig = window.basePath + '/assets/ckeditor/imagePluploadConfig.js';

	// The toolbar groups arrangement, optimized for two toolbar rows.
	config.toolbarGroups = [
		{ name: 'clipboard',   groups: [ 'clipboard', 'undo' ] },
		{ name: 'editing',     groups: [ 'find', 'selection', 'spellchecker' ] },
		{ name: 'links' },
		{ name: 'insert' },
		{ name: 'forms' },
		{ name: 'tools' },
		{ name: 'document',	   groups: [ 'mode', 'document', 'doctools' ] },
		{ name: 'others' },
		'/',
		{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
		{ name: 'paragraph',   groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ] },
		{ name: 'styles' },
		{ name: 'colors' },
		{ name: 'about' }
	];


	config.toolbar = 'article';
	config.toolbar_article = [
		{ items: [ 'Save' ] },
		{ name: 'styles', items: [ 'Format' ] },
		{ name: 'paragraph', groups: [ 'list', 'blocks' ], items: [ 'BulletedList', 'NumberedList', '-', 'Blockquote'] },
		{ name: 'styles', items: [ 'Styles' ] },
		{ name: 'links', items: [ 'Link', 'Unlink' ] },
		{ name: 'insert', items: [ 'Image', 'Flash', 'Table', 'Iframe'/*, 'Smiley', 'SpecialChar'*/ ] },
		{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ], items: [ 'Bold', 'Italic', 'Underline', 'Subscript' , 'Superscript',/* 'Strike',*/ '-', 'RemoveFormat' ] },
		//{ name: 'colors', items: [ 'TextColor' ] },
		{ name: 'undo', items: [ 'Undo', 'Redo' ] },
		{ name: 'clipboard', groups: [ 'clipboard' ], items: [ /*'Cut', 'Copy', 'Paste',*/ 'PasteText'/*, 'PasteFromWord'*/ ] },
		{ name: 'document', groups: [ 'document', 'mode' ], items: [ /*'NewPage', 'Preview', */'-', 'ShowBlocks', 'Source' ] },
		{ name: 'tools', items: [ 'Maximize' ] }
	];

	config.toolbar = 'album';
	config.toolbar_album = [
		{ items: [ 'Save' ] },
		/*{ name: 'styles', items: [ 'Format' ] },*/
		{ name: 'paragraph', groups: [ 'list', 'blocks' ], items: [ 'BulletedList', 'NumberedList', '-', 'Blockquote'] },
		{ name: 'styles', items: [ 'Styles' ] },
		{ name: 'links', items: [ 'Link', 'Unlink' ] },
		{ name: 'insert', items: [ 'Image'/*, 'SpecialChar'*/ ] },
		{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ], items: [ 'Bold', 'Italic', 'Underline', 'Subscript', 'Superscript',/* 'Strike',*/ '-', 'RemoveFormat' ] },
		//{ name: 'colors', items: [ 'TextColor' ] },
		{ name: 'undo', items: [ 'Undo', 'Redo' ] },
		{ name: 'clipboard', groups: [ 'clipboard' ], items: [ /*'Cut', 'Copy', 'Paste',*/ 'PasteText'/*, 'PasteFromWord'*/ ] },
		{ name: 'document', groups: [ 'document', 'mode' ], items: [ 'ShowBlocks' ] },
	];



	// Remove some buttons provided by the standard plugins, which are
	// not needed in the Standard(s) toolbar.
	// config.removeButtons = 'Underline,Subscript,Superscript';
	config.removeButtons = 'Underline';

	// Set the most common block elements.
	config.format_tags = 'p;h1;h2;h3;pre';

	// Simplify the dialog windows.
	config.removeDialogTabs = 'image:advanced;link:advanced';

	//config.removePlugins = 'autosave';
	//config.extraPlugins = 'imagePlupload,notification,autosave';
	//config.plugins = 'dialogui,dialog,a11yhelp,about,allowsave,autogrow,clipboard,autolink,basicstyles,blockquote,button,panelbutton,panel,floatpanel,colorbutton,menu,contextmenu,dialogadvtab,div,elementspath,enterkey,entities,popup,filebrowser,floatingspace,listblock,richcombo,format,horizontalrule,htmlwriter,image,indent,indentlist,fakeobjects,link,list,liststyle,magicline,maximize,nbsp,pagebreak,pastefromword,pastetext,removeformat,resize,menubutton,scayt,showblocks,showborders,sourcearea,specialchar,stylescombo,tab,table,tabletools,toolbar,undo,wsc,wysiwygarea,imagePlupload,notification,autosave';

	config.defaultLanguage = 'cs';

	config.imagePlupload_flash_swf_url = window.basePath + '/3rd/plupload-2.1.8/js/Moxie.swf';
	config.imagePlupload_silverlight_xap_url = window.basePath + '/3rd/plupload-2.1.8/js/Moxie.xap';

	// disable title on editing area
	config.title = false;


	config.entities = false;
	config.basicEntities = false;
	config.entities_greek = false;
	config.entities_latin = false;

	config.toolbar = 'article';
	config.stylesSet = 'article_styles';
	config.startupOutlineBlocks = false;


	config.autosave_disableNotifications = true;
};
