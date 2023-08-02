import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mohey.groupservice.detail.service.GroupDetailService;
import com.mohey.groupservice.entity.category.CategoryEntity;
import com.mohey.groupservice.entity.group.GenderOptionsEntity;
import com.mohey.groupservice.entity.group.GroupCoordinatesEntity;
import com.mohey.groupservice.entity.group.GroupEntity;
import com.mohey.groupservice.entity.group.GroupModifiableEntity;
import com.mohey.groupservice.entity.participant.GroupParticipantEntity;
import com.mohey.groupservice.repository.CategoryRepository;
import com.mohey.groupservice.repository.GenderOptionsRepository;
import com.mohey.groupservice.repository.GroupCoordinatesRepository;
import com.mohey.groupservice.repository.GroupDetailRepository;
import com.mohey.groupservice.repository.GroupModifiableRepository;
import com.mohey.groupservice.repository.GroupParticipantRepository;
import com.mohey.groupservice.detail.dto.GroupDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class GroupDetailServiceTest {

	@Mock
	private GroupDetailRepository groupDetailRepository;

	@Mock
	private GroupModifiableRepository groupModifiableRepository;

	@Mock
	private GroupCoordinatesRepository groupCoordinatesRepository;

	@Mock
	private GroupParticipantRepository groupParticipantRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private GenderOptionsRepository genderOptionsRepository;

	@InjectMocks
	private GroupDetailService groupDetailService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testGetGroupDetailByGroupId() {
		Long groupId = 1L;
		String groupUuid = "group_uuid";

		GroupEntity groupEntity = new GroupEntity();
		groupEntity.setId(groupId);
		groupEntity.setGroupUuid(groupUuid);
		groupEntity.setCreatedDatetime(LocalDateTime.now());

		GroupModifiableEntity groupModifiableEntity = new GroupModifiableEntity();
		groupModifiableEntity.setCategoryTbId(1L);
		groupModifiableEntity.setGenderOptionsTbId(1L);
		groupModifiableEntity.setGroupTbId(1L);
		groupModifiableEntity.setTitle("초밥먹자");
		groupModifiableEntity.setGroupStartDatetime(LocalDateTime.of(2023,8,03,12,30,00));
		groupModifiableEntity.setMaxParticipant(6);
		groupModifiableEntity.setLeaderUuid("kjh-1234-1234");
		groupModifiableEntity.setPrivateYn(false);
		groupModifiableEntity.setLat(123.2134);
		groupModifiableEntity.setLng(323.2134);
		groupModifiableEntity.setMinAge(10);
		groupModifiableEntity.setMaxAge(30);
		groupModifiableEntity.setLatestYn(true);
		groupModifiableEntity.setDescription("맛있는 초밥을 먹으러 가요");

		GroupCoordinatesEntity groupCoordinatesEntity = new GroupCoordinatesEntity();
		groupCoordinatesEntity.setGroupTbId(1L);
		groupCoordinatesEntity.setId(1L);
		groupCoordinatesEntity.setLocationId("강남구 멀티캠퍼스");
		groupCoordinatesEntity.setCreatedDatetime(LocalDateTime.now());

		List<GroupParticipantEntity> groupParticipantEntities = new ArrayList<>();
		GroupParticipantEntity participant1 = new GroupParticipantEntity();
		GroupParticipantEntity participant2 = new GroupParticipantEntity();
		GroupParticipantEntity participant3 = new GroupParticipantEntity();
		groupParticipantEntities.add(participant1);
		groupParticipantEntities.add(participant2);
		groupParticipantEntities.add(participant3);

		CategoryEntity categoryEntity = new CategoryEntity();
		categoryEntity.setId(1L);
		categoryEntity.setCategoryName("TestCategory");

		GenderOptionsEntity genderOptionsEntity = new GenderOptionsEntity();
		genderOptionsEntity.setId(1L);
		genderOptionsEntity.setGenderDescription("TestGender");

		// Mock 객체에게 동작을 설정
		when(groupDetailRepository.findByGroupUuid(groupUuid)).thenReturn(groupEntity);
		when(groupModifiableRepository.findLatestGroupModifiableByGroupId(groupId)).thenReturn(groupModifiableEntity);
		when(groupCoordinatesRepository.findByGroupTbIdAndCreatedDatetime(groupId, groupModifiableEntity.getCreatedDatetime())).thenReturn(groupCoordinatesEntity);
		when(groupParticipantRepository.findByGroupIdAndGroupParticipantStatusIsNull(groupId)).thenReturn(groupParticipantEntities);
		when(categoryRepository.findById(groupModifiableEntity.getCategoryTbId())).thenReturn(categoryEntity);
		when(genderOptionsRepository.findById(groupModifiableEntity.getGenderOptionsTbId())).thenReturn(genderOptionsEntity);

		// 서비스 메서드 호출
		GroupDto groupDto = groupDetailService.getGroupDetailByGroupId(groupUuid);

		// 결과 검증
		assertNotNull(groupDto);
		assertEquals(groupId, groupDto.getGroupId());
		assertEquals(3, groupDto.getParticipantsNum());
		assertEquals("맛있는 초밥을 먹으러 가요", groupDto.getGroupDescription());
		assertEquals("강남구 멀티캠퍼스", groupDto.getLocationId());
		assertEquals("TestCategory", groupDto.getCategory());
		assertEquals("TestGender", groupDto.getGenderOptions());
		// 나머지 필드들에 대해서도 적절한 검증
	}
}