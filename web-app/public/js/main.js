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

  function cloneObservable(observableObject) {
    return ko.mapping.fromJS(ko.toJS(observableObject));
  }
});

window.columnTypes = function() {
  return $("#datamodel").data("types");
};