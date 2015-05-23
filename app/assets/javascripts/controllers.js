//"use strict";

/** Controllers */
angular.module('sseChat.controllers', ['sseChat.services']).
    controller('TodoCtrl', function ($scope, $http, todoModel) {
        $scope.tasks = todoModel.getTasks();

        $scope.completeTask = function () {

        };
    });