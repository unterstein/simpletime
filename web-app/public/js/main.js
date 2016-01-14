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

window.pickElement = function(entry, key) {
  switch(key()) {
    case "start":
      return entry.start();
    case "end":
      return entry.end();
    default:
      return entry.props().filter(function(element) { return element.key() == key();});
  }
};