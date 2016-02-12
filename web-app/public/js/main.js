$(function () {

  ko.bindingHandlers.datepicker = {
    init: function (element, valueAccessor, allBindingsAccessor) {
      //initialize datepicker with some optional options
      var options = allBindingsAccessor().datepickerOptions || {
            format: 'DD/MM/YYYY HH:mm',
            defaultDate: moment(valueAccessor()(), "x"),
            sideBySide: true,
            calendarWeeks: true,
            showTodayButton: true,
            showClear: true,
            showClose: true,
            toolbarPlacement: "top",
            stepping: 15
          };
      $(element).datetimepicker(options);
      //when a user changes the date, update the view model
      ko.utils.registerEventHandler(element, "dp.change", function (event) {
        var value = valueAccessor();
        if (ko.isObservable(value)) {
          value(event.date.unix() * 1000);
        }
      });
    },
    update: function (element, valueAccessor) {
      var widget = $(element).data("datepicker");
      //when the view model is updated, update the widget
      if (widget) {
        widget.date = ko.utils.unwrapObservable(valueAccessor());
        if (widget.date) {
          widget.setValue();
        }
      }
    }
  };

  var datamodel = $("#datamodel");

  if (datamodel.data("page") == "project") {
    var model = datamodel.data("model");
    var column = datamodel.data("column");
    var viewModel = ko.mapping.fromJS(model);

    ko.applyBindings(viewModel);

    $("#newColumn").click(function () {
      viewModel.columns.push(cloneObservable(column));
      updateNames();
      return false;
    });

    $(document).on("click", ".deleteColumn", function () {
      var index = $(".deleteColumn").index(this);
      viewModel.columns.remove(function (column) {
        return $($(".deleteColumn")[index].closest("tr")).find(":input:first").val() == column.columnKey();
      });
      updateNames();
      return false;
    });

    updateNames();

    function updateNames() {
      var columns= $(".columns");
      columns.each(function () {
        var index = columns.index(this);
        $(this).find(".columnName").attr("name", "columns[" + index + "].columnName");
        $(this).find(".columnType").attr("name", "columns[" + index + "].columnType");
        $(this).find(".columnKey").attr("name", "columns[" + index + "].columnKey");
      });
    }
  }

  if (datamodel.data("page") == "details") {
    var model = datamodel.data("model");
    var entry = datamodel.data("entry");
    var viewModel = ko.mapping.fromJS(model);

    ko.applyBindings(viewModel);

    $("#newEntry").click(function () {
      viewModel.entries.push(cloneObservable(entry));
      updateColumnNames();
      $(".focus-marker").find("tr:last :input:visible:first").focus().selectAll();
      return false;
    });

    updateColumnNames();

    function updateColumnNames() {
      var columns = $(".columns");
      columns.each(function () {
        var index = columns.index(this);
        $(this).find(".numerate").each(function () {
          var type = $(this).data("entrytype");
          $(this).attr("name", "entries[" + index + "]." + type)
        });
      });
    }
  }

  function cloneObservable(observableObject) {
    return ko.mapping.fromJS(ko.toJS(observableObject));
  }

  //function initDatePicker() {
  //  $(".datetime").each(function (i, elem) {
  //    var data = $(elem).data("DateTimePicker");
  //    if (data != undefined) {
  //      data.destroy();
  //    }
  //    $(elem).datetimepicker({
  //      format: 'DD/MM/YYYY HH:mm'
  //    });
  //  });
  //}
});

window.columnTypes = function() {
  return $("#datamodel").data("types");
};

window.pickType = function(columnType) {
  switch(columnType()) {
    case "BOOLEAN":
      return "checkbox";
    default:
      return "text";
  }
};

window.pickClass = function(columnType) {
  switch(columnType()) {
    case "TIME":
      return "form-control datetime";
    default:
      return "form-control numerate";
  }
};

window.pickTableClass = function(columnType) {
  switch(columnType()) {
    case "HIDDEN":
      return "table-hidden";
    default:
      return "";
  }
};

window.pickElement = function(entry, key) {
  switch(key()) {
    case "id":
      return entry.id;
    case "start":
      return entry.start;
    case "end":
      return entry.end;
    default:
      var element = entry.props().filter(function(element) { return element.key() == key();});
      if (element.size == 1) {
        return element[0].value;
      } else {
        return "";
      }
  }
};

/**
 * Select behavior
 */
(function () {
  $.fn.setCursorPosition = function (position) {
    if (this.length == 0) return this;
    return $(this).setSelection(position, position);
  };

  $.fn.setSelection = function (selectionStart, selectionEnd) {
    if (this.length == 0) return this;
    input = this[0];

    if (input.createTextRange) {
      var range = input.createTextRange();
      range.collapse(true);
      range.moveEnd('character', selectionEnd);
      range.moveStart('character', selectionStart);
      range.select();
    } else if (input.setSelectionRange) {
      input.focus();
      input.setSelectionRange(selectionStart, selectionEnd);
    }

    return this;
  };

  $.fn.focusEnd = function () {
    this.setCursorPosition(this.val().length);
  };

  $.fn.selectAll = function () {
    if (this.val() != undefined) {
      this.setSelection(0, this.val().length);
    }
  };

  $.fn.endsWith = function (suffix) {
    if (this.val() != undefined) {
      return this.val().toLowerCase().match(suffix.toLowerCase() + "$") == suffix.toLowerCase();
    } else {
      return false;
    }
  }
})();