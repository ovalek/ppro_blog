jQuery(document).ready(function($) {
	var $sections = $('#sections');

	var refreshAddButtons = function(element) {
		element = typeof element !== 'undefined' ? element : $sections;
		element.find('li[data-item-id]').each(function() {
			$item = $(this);
			limit = $item.closest('[data-depth-limit]').length ? $item.closest('[data-depth-limit]').attr('data-depth-limit') : false;
			$itemAddChildBtn = $item.find('> div.head > div.btnContainer > .addChildBtn');
			if (limit) {
				if ($itemAddChildBtn.length) {
					var bonus = 0;
					if (!$item.is('[data-depth-limit]')) {
						bonus = 1;
					}
					if ($item.parentsUntil('li[data-depth-limit]', 'li[data-item-id]').length + bonus < limit) {
						$itemAddChildBtn.show();
					} else {
						$itemAddChildBtn.hide();
					}
				}
			} else {
				$itemAddChildBtn.show();
			}
		});
	};

	refreshAddButtons($sections);


	$sections.nestedSortable({
		listType: 'ul',
		handle: 'div',
		items: 'li',
		toleranceElement: '> div',
		protectRoot: true,
		opacity: .6,
		placeholder: 'placeholder',
		disableNestingClass: 'noChildren',
		revert: 250,
		tabSize: 25,
		tolerance: 'pointer',
		isAllowed: function (item, parent) {
			if ($(parent).is('[data-depth-limit]')) {
//                        console.log('selfLimit: '+$(parent).attr('data-depth-limit'));
//                        console.log('dragenTreeDepth: '+($('#sections li.ui-sortable-helper').depth('li')+1));
				if (($('#sections li.ui-sortable-helper').depth('li[data-item-id]') + 1) <= $(parent).attr('data-depth-limit')) {
					return true;
				} else {
					return false;
				}
			} else if ($(parent).parentsUntil('#sections', 'li[data-depth-limit]').length >= 1) {
//                        console.log('limit -> parent: '+($(parent).parentsUntil('li[data-depth-limit]:first', 'li').length + 1));
//                        console.log('dragenTreeDepth: '+($('#sections li.ui-sortable-helper').depth('li') + 1));
//                        console.log('limit: '+$(parent).closest('[data-depth-limit]').attr('data-depth-limit'));
				if (($(parent).parentsUntil('li[data-depth-limit]', 'li[data-item-id]').length + 1) + ($('#sections li.ui-sortable-helper').depth('li[data-item-id]') + 1) <= $(parent).closest('[data-depth-limit]').attr('data-depth-limit')) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		},
		update: function (event, ui) {
			$sections.addClass('noDirectNesting');
			$('#sections .btnContainer').hide();

			$("#structureSaveBtn").addClass("btn-success");
			window.onbeforeunload = function () {
				return "Rozvržení položek nebylo uloženo. Chcete přesto pokračovat?";
			};
		}
	});



	$sections.on('click', 'a[data-item-toggle]', function(event){
		event.preventDefault();
		var $that = $(this);
		// ajax loader animation
		var $loaderEl = $('<span class="btn-sm glyphicon glyphicon-refresh glyphicon-refresh-animate" title="Probíhá požadavek..."></span>');
		$that.hide().after($loaderEl);
		$.ajax({
			type: "POST",
			url: $that.attr("href"),
			success: function(response){
				var json = $.my.checkResponse(response);
				if (isSet(json.status) && json.status) {
					$loaderEl.remove();
					$that.replaceWith(json.toggleButton);
					$(".btnContainer a[data-toggle=tooltip][title]").tooltip({placement:$(this).attr('data-placement')});
				} else {
					$loaderEl.remove();
					$that.show();
					$.my.notify('Došlo k chybě při úpravě viditelnosti.', 'error');
				}
			},
			error: function(){
				$loaderEl.remove();
				$that.show();
				alert('Chyba při spojení. Zkuste obnovit stránku.');
			}
		});
	});



	var getItems = function(element, parentID, parentOrder, orderArray) {
		element = typeof element !== 'undefined' ? element : $sections;
		parentID = typeof parentID !== 'undefined' ? parentID : null;
		parentOrder = typeof parentOrder !== 'undefined' ? parentOrder : '';
		if (parentOrder !== '') {
			parentOrder = parentOrder + '/';
		}
		orderArray = typeof orderArray !== 'undefined' ? orderArray : [];
		var items = $(element).find('> [data-item-id]');
		var digitsCount = items.length.toString().length;
		var i = 0;
		$(items).each(function() {
			i++;
			orderArray.push({'id':$(this).attr('data-item-id'), 'parent_id':parentID, 'order':parentOrder + fill(i, digitsCount)});
			if ($(this).children('ul').length > 0) {
				getItems($(this).children('ul'), $(this).attr('data-item-id'), parentOrder + i, orderArray);
			}
		});
		return orderArray;
	};

	var saveStructure = function() {
		var $saveBtnEl = $('#structureSaveBtn');
		var $sectionsEl = $("#sections");

		$saveBtnEl.addClass('disabled');
		$sectionsEl.nestedSortable("disable");

		var saveStructureURL = $saveBtnEl.attr('data-save-structure-url');

		var $loaderEl = $('<span class="circleLoader glyphicon glyphicon-cog glyphicon-cog-animate" title="Probíhá požadavek..."></span>');
		$saveBtnEl.after($loaderEl);

		$.ajax({
			type: "POST",
			url: saveStructureURL,
			data:{serializedStructure:JSON.stringify(getItems())},
			success: function(response){
				$sectionsEl.nestedSortable( "enable" );
				$saveBtnEl.removeClass('disabled');

				var json = $.my.checkResponse(response);
				if (isSet(json.status) && json.status) {
					$saveBtnEl.removeClass("btn-success");
					window.onbeforeunload = null;
					// info
					$.my.notify(json.msg, "success");
				} else {
					$.my.notify(json.msg, "error");
				}

				$loaderEl.remove();

				refreshAddButtons($sections);
				$('#sections .btnContainer').show();
			},
			error: function() {
				$loaderEl.remove();
				$.my.notify("Nepodařilo se spojit se serverem, zkuste to za chvíli znovu.", "fatalError");
				$sectionsEl.nestedSortable( "refreshPositions" );
				$sectionsEl.nestedSortable( "enable" );
				$saveBtnEl.removeClass('disabled');

				refreshAddButtons($sections);
				$('#sections .btnContainer').show();
			}
		});
	};

	$('#sectionsButtons').on('click', '#structureSaveBtn', function(event) {
		event.preventDefault();
		$(this).blur();
		if (!$(this).hasClass('disabled')) {
			saveStructure();
		}
	});



	$sections.on('click', 'a[data-item-delete]', function(event){
		event.preventDefault();
		var location = $(this).attr("data-href");
		$.my.confirmDialogWC(
			'confirm',
			'danger',
			'Potvrzení',
			'Opravdu chcete položku <strong>'+$(this).attr("data-name")+'</strong> včetně všech vnořených položek smazat?',
			'storno',
			function(){
				window.location.replace(location);
			}
		);
	});
});