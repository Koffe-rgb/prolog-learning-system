:- use_module(memory, [ pls_put_arg/1,
                        pls_get_res/1,
                        pls_add_expected_res/1,
                        pls_add_test/1,
                        pls_assert/2
                      ]).

:- begin_tests('{{taskName}}').

{{#each testNames}}
setup_{{this}} :- pls_add_test('{{this}}').

cleanup_{{this}} :- true.

test( '{{this}}', [setup(setup_{{this}}), cleanup(cleanup_{{this}})] ) :-
    fail.
{{/each}}

:- end_tests('{{taskName}}').

% Пример формирования задания:
% test ( 'название_теста', setup( pls_add_test('название_теста') ) ) :-
%     random_between(0, 10, A),       % Генерация первого аргумента
%     pls_put_arg(A),                 % Поместить первый аргумент в список входных данных
%     random_between(0, 10, B),       % Генерация второго аргумента
%     pls_put_arg(B),                 % Поместить второй аргумент в список входных данных
%     Result is A + B,                % Вычисление результата
%     pls_add_expected_res(Result),   % Обозначиваем ожидаемый результат решения
%     run,                            % Запускаем решение студента
%     pls_get_res(Answer),            % Получаем результат студента
%     pls_assert(Result, Answer).     % Сравнение ожидаемого и полученного результата