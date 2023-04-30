:- use_module(memory, [pls_pass_java_obj_ref/1]).


load_context(Task, Solution) :-
    [ memory, Task, Solution ].


start_tests :-
    pls_setup,
    call_cleanup(run_tests, pls_clear_db).