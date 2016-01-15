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
      return false;
    });

    $(document).on("click", ".deleteColumn", function () {
      $(".deleteColumn").index(this);
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
      $(".focus-marker").find("tr:last :input:first").focus().selectAll();
      return false;
    });
  }

  function cloneObservable(observableObject) {
    return ko.mapping.fromJS(ko.toJS(observableObject));
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

window.pickElement = function(entry, key) {
  switch(key()) {
    case "start":
      return entry.start();
    case "end":
      return entry.end();
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