$(function () {
  var datamodel = $("#datamodel");

  if (datamodel.data("page") == "project") {
    var model = datamodel.data("model");
    var column = datamodel.data("column");
    var viewModel = ko.mapping.fromJS(model);

    ko.applyBindings(viewModel);

    $("#newColumn").click(function () {
      viewModel.columns.push(cloneObservable(column));
      updateNames();
      initComponents();
      return false;
    });

    $(document).on("click", ".deleteColumn", function () {
      $(".deleteColumn").index(this);
      updateNames();
      initComponents();
      return false;
    });

    updateNames();
    initComponents();

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
      initDatePicker();
      updateColumnNames();
      $(".focus-marker").find("tr:last :input:visible:first").focus().selectAll();
      return false;
    });

    updateColumnNames();

    function updateColumnNames() {
      var columns = $(".columns");
      columns.each(function () {
        var index = columns.index(this);
        $(this).find(".form-control").each(function () {
          var type = $(this).attr("entryType");
          $(this).attr("name", "entries[" + index + "]." + type)
        });
      });
    }
  }

  function cloneObservable(observableObject) {
    return ko.mapping.fromJS(ko.toJS(observableObject));
  }

  function initComponents() {
    initDatePicker();
  }

  function initDatePicker() {
    $(".datetime").each(function (i, elem) {
      var data = $(elem).data("DateTimePicker");
      if (data != undefined) {
        data.destroy();
      }
      $(elem).datetimepicker();
    });
  }
});

window.columnTypes = function() {
  return $("#datamodel").data("types");
};

window.pickType = function(columnType) {
  switch(columnType()) {
    case "STRING":
      return "text";
    case "BOOLEAN":
      return "checkbox";
    case "TIME":
      return "text";
    case "ENUM":
      return "text"; // TODO
    default:
      return "text";
  }
};

window.pickClass = function(columnType) {
  switch(columnType()) {
    case "TIME":
      return "form-control datetime";
    default:
      return "form-control";
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
      return entry.id();
    case "start":
      return new Date(entry.start());
    case "end":
      return new Date(entry.end());
    default:
      return entry.props().filter(function(element) { return element.key() == key();})[0].value();
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