jQuery(document).ready(function($) {
	CKEDITOR.disableAutoInline = true;
	$('div.editableArticle').each(function(index, editableArticle) {
		var $ea = $(editableArticle);
		var ed = false;
		var $tb = $('<div class="articleToolbar"></div>');
		var $toggleBtn = $('<a class="btn btn-xs btn-default glyphicon glyphicon-edit" title="Aktivovat editor"></a>');

		$tb.html($toggleBtn);
		$ea.before($tb);

		$toggleBtn.on('click', function(event) {
			event.preventDefault();
			if ($ea.hasClass('enabled')) {
				if (ed) {
					if (ed.editor.checkDirty()) {
						saveDialog($ea, ed, $tb, $toggleBtn);
					}
					ed.editor.destroy(true);
				}
				$ea.attr('contenteditable', 'false').removeClass('enabled');
				$toggleBtn.removeClass('btn-primary');
				$toggleBtn.addClass('glyphicon-lock').attr('title', 'Aktivovat editor');

				window.initLightbox();
			} else {
				ed = enableEditor($ea, $tb, $toggleBtn);

				$toggleBtn.removeClass('glyphicon-lock');
				$toggleBtn.addClass('btn-primary').attr('title', 'Vypnout editor');
				$ea.addClass('enabled')

				window.removeLightbox();

				$ea.editor.on('change', function() {
					$('.article a:has(img), .editableArticle a:has(img)').addClass('imgLink');
				});

					//$ea.trigger('focus');
			}
		});
	});



	var saveDialog = function($ea, ed, $tb, $toggleBtn) {
		$.my.confirmDialogWC(
			'confirm',
			'primary',
			'Uložit článek?',
			'Obsah byl změněn. <strong>Chcete článek uložit?</strong>',
			'storno',
			function(dialog){
				saveContent($ea, ed, $toggleBtn);
				dialog.close();
			},
			function (dialog){
				localStorage.removeItem(ed.editor.config.autosave_SaveKey);
			}
		);
	};



	var saveContent = function($ea, ed, $toggleBtn) {
		// ajax loader animation
		var $loaderEl = $('<span class="btn-sm glyphicon glyphicon-refresh glyphicon-refresh-animate" title="Probíhá načítání editoru..."></span>');
		$toggleBtn.hide().after($loaderEl);
		$.ajax({
			type: "POST",
			url: $ea.attr("data-save-link"),
			data:{content:ed.editor.getData()},
			success: function(response){
				var json = $.my.checkResponse(response);
				if (isSet(json.status) && json.status) {
					ed.editor.resetDirty();
					localStorage.removeItem(ed.editor.config.autosave_SaveKey);
				}
				$loaderEl.remove();
				$toggleBtn.show();
				$.my.notify(json.msg, json.msgType);
			},
			error: function(){
				alert('Chyba při spojení. Zkuste obnovit stránku.');
			}
		});
	};



	var enableEditor = function($ea, $tb, $toggleBtn) {
		var config = $.parseJSON($ea.attr('data-ck-config'));

		// ajax loader animation
		var $loaderEl = $('<span class="btn-sm glyphicon glyphicon-refresh glyphicon-refresh-animate" title="Probíhá načítání editoru..."></span>');
		$toggleBtn.hide().after($loaderEl);

		var ed = $ea.ckeditor(function() {
			//editor.editor.on('change', function() {
			//	console.log('change');
			//});

			ed.editor.on('save', function() {
				saveContent($ea, ed, $toggleBtn);
				// disable editor
				if (ed) {
					ed.editor.destroy(true);
				}
				$ea.attr('contenteditable', 'false').removeClass('enabled');
				$toggleBtn.removeClass('btn-primary');
				$toggleBtn.addClass('glyphicon-lock').attr('title', 'Aktivovat editor');
			});

			//$ea.attr('contenteditable', 'true');
			ed.editor.setReadOnly(false);
			$loaderEl.remove();
			$toggleBtn.show();

			$ea.trigger('focus');
		}, config);

		return ed;
	};
});