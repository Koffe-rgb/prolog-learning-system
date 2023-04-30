% перед использованием в системе, необходимо преобразовать в QLF файл
% с помощью предиката qcompile/1, после чего поместить в папку
% рабочего пространства Студента (student_workspace)


:- module(memory, [ pls_setup/0,
                    pls_clear_db/0,
                    pls_pass_java_obj_ref/1,
                    get_arg/1,
                    pls_put_arg/1,
                    pls_get_res/1,
                    put_res/1,
                    pls_add_expected_res/1,
                    pls_add_real_res/1,
                    pls_set_description/1,
                    pls_add_test/1,
                    pls_assert/2
                  ]).

:- dynamic
    task_arguments/1,
    task_results/1,
    java_obj_ref/1,
    cur_test_name/1.





%-------------------------------------------------------------------------------
% служебные предикаты тестирования, используются для настройки заданий

pls_add_test(TestName) :-
    java_obj_ref(Ref),
    cur_test_name(CurName),
    retract(cur_test_name(CurName)),
    assertz(cur_test_name(TestName)),
    jpl_call(Ref, addNewTest, [TestName], @(void)).


% deprecated метод. Используйте возможности графического интерфейса среды Преподаватля
pls_set_description(Desc) :-
    java_obj_ref(Ref),
    jpl_call(Ref, setDescription, [Desc], @(void)).


pls_add_info_arg(Arg) :-
    java_obj_ref(Ref),
    cur_test_name(TestName),
    term_to_atom(Arg, ArgAtom),
    jpl_call(Ref, addInfoArgument, [ArgAtom, TestName], @(void)).


pls_add_real_res(Res) :-
    java_obj_ref(Ref),
    cur_test_name(TestName),
    term_to_atom(Res, ResAtom),
    jpl_call(Ref, addRealResult, [ResAtom, TestName], @(void)).


%-------------------------------------------------------------------------------
% предикат для установления связи между JVM и SWI-Prolog

pls_pass_java_obj_ref(Jref) :-
    assertz( java_obj_ref(Jref) ).


%-------------------------------------------------------------------------------
% предикаты настройки контекста тестирования

pls_setup :-
    assertz( task_arguments( [] ) ),
    assertz( task_results( [] ) ),
    assertz( cur_test_name('') ).


pls_clear_db :-
    retractall( task_arguments(_) ),
    retractall( task_results(_) ),
%    retractall( java_obj_ref(_) ),
    retractall( cur_test_name(_) ).


%-------------------------------------------------------------------------------
% предикаты для пользователей

% предикат для Студента. Позволяет последовательно получать аргументы задания
get_arg(Arg) :-
    task_arguments(List),
    List = [Arg | Tail],
    retract( task_arguments(List) ),
    assertz( task_arguments(Tail) ).


% предикат для Студента. Позволяет последовательно добавлять результаты
put_res(Res) :-
    task_results(List),
    retract( task_results(List) ),
    append( List, [Res], NewList ),
    assertz( task_results(NewList) ),
    pls_add_real_res(Res).


% предикат для Преподавателя. Позволяет последовательно добавлять аргументы в задание
pls_put_arg(Arg) :-
    task_arguments(List),
    retract( task_arguments(List) ),
    append( List, [Arg], NewList ),
    assertz( task_arguments(NewList) ),
    pls_add_info_arg(Arg).


% предикат для Преподавателя. Позволяет последовательно получать результаты решения Студента
pls_get_res(Res) :-
    task_results(List),
    List = [Res | Tail],
    retract( task_results(List) ),
    assertz( task_results(Tail) ).


% предикат для Преподавателя. Позволяет объявить ожидаемый результат для задания
pls_add_expected_res(Res) :-
    java_obj_ref(Ref),
    cur_test_name(TestName),
    term_to_atom(Res, ResAtom),
    jpl_call(Ref, addInfoResult, [ResAtom, TestName], @(void)).


% предикат для Преподавателя. Позволяет сравнить два результата и отправить этот статус теста в JVM
pls_assert(Res1, Res2) :-
    java_obj_ref(Ref),
    cur_test_name(CurName),
    (   Res1 == Res2 ->
        jpl_call(Ref, setTestComplete, [@(true), CurName], @(void)),
        true
    ;   jpl_call(Ref, setTestComplete, [@(false), CurName], @(void)),
        false
    ).

