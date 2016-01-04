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
      var columnNames = $(".columnName");
      columnNames.each(function () {
        $(this).attr("name", "columns[" + columnNames.index(this) + "].columnName");
      });
      var columnTypes = $(".columnType");
      columnTypes.each(function () {
        $(this).attr("name", "columns[" + columnTypes.index(this) + "].columnType");
      });
    }
  }

  function cloneObservable(observableObject) {
    return ko.mapping.fromJS(ko.toJS(observableObject));
  }
});