$(function () {
  var datamodel = $("#datamodel");

  if (datamodel.data("page") == "project") {
    var model = datamodel.data("model");
    var column = datamodel.data("column");
    var viewModel = ko.mapping.fromJS(model);

    ko.applyBindings(viewModel);

    $("#newColumn").click(function () {
      viewModel.columns.push(cloneObservable(column));
      return false;
    });

    $(document).on("click", ".deleteColumn", function () {
      var removeItem = $(".deleteColumn").index(this);
      console.log(removeItem);
      return false;
    });
  }

  function cloneObservable(observableObject) {
    return ko.mapping.fromJS(ko.toJS(observableObject));
  }
});