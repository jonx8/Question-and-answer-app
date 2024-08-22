insert into questions(id, title, text, status)
values (1, 'Question title', 'Far far away, behind', 'PUBLISHED'),
       (2, 'How i met your mother?', 'Lorem ipsum dolor si', 'DRAFT');

insert into answers(text, question_id)
values ('Far far away, behind the word mountains, far from the countries Vokalia' ||
           ' and Consonantia, there live the blind texts. Separated they live in Bookmarksgrove ' ||
           'right at the coast of the Semantics, a large.', 1);