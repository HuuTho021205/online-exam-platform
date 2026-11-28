package com.onlineexam.platform.service.implement;

import com.onlineexam.platform.dto.QuestionRequest;
import com.onlineexam.platform.dto.QuestionResponse;
import com.onlineexam.platform.entity.Choice;
import com.onlineexam.platform.entity.Question;
import com.onlineexam.platform.entity.Subject;
import com.onlineexam.platform.exception.BusinessRuleException;
import com.onlineexam.platform.exception.ResourceNotFoundException;
import com.onlineexam.platform.repository.*;
import com.onlineexam.platform.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;
    private final SubjectRepository subjectRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamAttemptRepository examAttemptRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponse> getAllQuestion(String search, Pageable pageable, Long subjectId) {
        String escapedKeyword = escapeLikeKeyword(search);
        return questionRepository.searchQuestions(subjectId, escapedKeyword, pageable)
                .map(q -> {
                    Long qId = q.getQuestionId();
                    boolean inExam = examQuestionRepository.existsByQuestion_QuestionId(qId);
                    boolean hasAttempts = inExam && examAttemptRepository.existsAttemptByQuestionId(qId);
                    return new QuestionResponse(
                            qId,
                            q.getContent(),
                            q.getSubject() != null ? q.getSubject().getSubjectName() : "—",
                            inExam,
                            hasAttempts
                    );
                });
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionRequest getQuestionById(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "questionId", questionId));
        List<Choice> choices = choiceRepository.findByQuestion_QuestionIdOrderByChoiceIdAsc(questionId);
        if (choices.size() != 4) {
            throw new BusinessRuleException(
                    "Invalid choices count for questionId=" + questionId + ", count=" + choices.size(),
                    "Dữ liệu câu hỏi không hợp lệ (không đủ 4 đáp án trong cơ sở dữ liệu)"
            );
        }
        List<String> options = new ArrayList<>(4);
        String correctOption = null;
        for (int i = 0; i < 4; i++) {
            Choice choice = choices.get(i);
            options.add(choice.getContent());
            if (choice.isCorrect()) {
                correctOption = String.valueOf((char) ('A' + i));
            }
        }
        if (correctOption == null) {
            throw new BusinessRuleException(
                    "Missing correct choice for questionId=" + questionId,
                    "Dữ liệu câu hỏi bị lỗi (chưa xác định đáp án đúng trong cơ sở dữ liệu)"
            );
        }
        boolean inExam = examQuestionRepository.existsByQuestion_QuestionId(questionId);
        boolean hasAttempts = inExam && examAttemptRepository.existsAttemptByQuestionId(questionId);
        return new QuestionRequest(
                question.getContent(),
                options,
                correctOption,
                question.getSubject().getSubjectId(),
                inExam,
                hasAttempts
        );
    }

    @Override
    @Transactional
    public void createQuestion(QuestionRequest request) {
        validateDistinctOptions(request.getOptions());

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "subjectId", request.getSubjectId()));

        Question question = new Question();
        question.setContent(request.getContent().trim());
        question.setSubject(subject);
        Question savedQuestion = questionRepository.save(question);

        int correctIndex = request.getCorrectOption().charAt(0) - 'A';
        List<Choice> choices = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            Choice choice = new Choice();
            choice.setContent(request.getOptions().get(i).trim());
            choice.setCorrect(i == correctIndex);
            choice.setQuestion(savedQuestion);
            choices.add(choice);
        }
        choiceRepository.saveAll(choices);
    }

    @Override
    @Transactional
    public void updateQuestion(Long questionId, QuestionRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "questionId", questionId));
        boolean inExam = examQuestionRepository.existsByQuestion_QuestionId(questionId);
        boolean hasAttempts = inExam && examAttemptRepository.existsAttemptByQuestionId(questionId);
        if (hasAttempts) {
            throw new BusinessRuleException(
                    "Cannot update question with attempts: questionId=" + questionId,
                    "Câu hỏi đã có lượt làm bài của sinh viên, không được phép chỉnh sửa"
            );
        }
        if (inExam && !question.getSubject().getSubjectId().equals(request.getSubjectId())) {
            throw new BusinessRuleException(
                    "Cannot change subject of question in exam: questionId=" + questionId,
                    "Câu hỏi đã được đưa vào kỳ thi, không được phép thay đổi môn học"
            );
        }
        validateDistinctOptions(request.getOptions());
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "subjectId", request.getSubjectId()));
        List<Choice> existingChoices = choiceRepository.findByQuestion_QuestionIdOrderByChoiceIdAsc(questionId);
        if (existingChoices.size() != 4) {
            throw new BusinessRuleException(
                    "Corrupted choices data for questionId=" + questionId + ", count=" + existingChoices.size(),
                    "Dữ liệu đáp án trong cơ sở dữ liệu không hợp lệ (không đủ 4 đáp án), không thể cập nhật"
            );
        }
        question.setContent(request.getContent().trim());
        question.setSubject(subject);
        questionRepository.save(question);
        int correctIndex = request.getCorrectOption().charAt(0) - 'A';
        for (int i = 0; i < 4; i++) {
            Choice choice = existingChoices.get(i);
            choice.setContent(request.getOptions().get(i).trim());
            choice.setCorrect(i == correctIndex);
        }
        choiceRepository.saveAll(existingChoices);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new ResourceNotFoundException("Question", "questionId", questionId);
        }
        if (examQuestionRepository.existsByQuestion_QuestionId(questionId)) {
            throw new BusinessRuleException(
                    "Cannot delete question assigned to exam: questionId=" + questionId,
                    "Không thể xóa câu hỏi đã được đưa vào kỳ thi"
            );
        }
        questionRepository.deleteById(questionId);
    }

    private void validateDistinctOptions(List<String> options) {
        Set<String> seen = new HashSet<>();
        for (String opt : options) {
            String normalized = opt.trim().toLowerCase();
            if (!seen.add(normalized)) {
                throw new BusinessRuleException(
                        "Duplicate option content detected",
                        "Nội dung 4 đáp án không được trùng nhau"
                );
            }
        }
    }

    private String escapeLikeKeyword(String search) {
        if (search == null || search.isBlank()) {
            return null;
        }
        return search.trim()
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
    }
}