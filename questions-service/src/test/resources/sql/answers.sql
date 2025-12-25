insert into questions(id, title, text, author)
values (1, 'How i met your mother?', 'Lorem ipsum dolor si', '9bce5101-38d7-462d-a891-047f6c1b6129'),
       (2, 'Question title 2', 'Text text text', 'e95f8551-8bd3-477b-85b5-a3d4a5c143a8');

insert into answers(id, text, question_id, author)
values (1, 'Far far away, behind the word mountains, far from the countries Vokalia' ||
           ' and Consonantia, there live the blind texts. Separated they live in Bookmarksgrove ' ||
           'right at the coast of the Semantics, a large.', 1, '9bce5101-38d7-462d-a891-047f6c1b6129'),
       (2, 'Far far away, behind the word mountains, far from the countries Vokalia' ||
           ' and Consonantia, there live the blind texts. Separated they live in Bookmarksgrove ' ||
           'right at the coast of the Semantics, a large.', 1, 'e95f8551-8bd3-477b-85b5-a3d4a5c143a8');
